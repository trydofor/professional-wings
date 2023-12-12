package pro.fessional.wings.faceless.jooqgen;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.codegen.GeneratorStrategy;
import org.jooq.codegen.JavaGenerator;
import org.jooq.codegen.JavaWriter;
import org.jooq.impl.DAOImpl;
import org.jooq.meta.CatalogDefinition;
import org.jooq.meta.ColumnDefinition;
import org.jooq.meta.Definition;
import org.jooq.meta.SchemaDefinition;
import org.jooq.meta.TableDefinition;
import org.jooq.meta.TypedElementDefinition;
import org.jooq.meta.UDTDefinition;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.mirana.time.DateNumber;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.database.jooq.WingsJooqDaoAliasImpl;
import pro.fessional.wings.faceless.database.jooq.WingsJooqDaoJournalImpl;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_COMMIT_ID;
import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_DELETE_DT;
import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_IS_DELETED;

/**
 * <pre>
 * When upgrading jooq, you need to check the compatibility of Override and Reflection methods.
 * The code between `🦁>>>` and `🦁<<<` doesn't need to be checked.
 * Only java is supported, kotlin and scala are not.
 * </pre>
 *
 * @author trydofor
 * @since 2019-05-31
 */
public class WingsJavaGenerator extends JavaGenerator {

    private GeneratorStrategy proxyStrategy = null;

    @Override
    public GeneratorStrategy getStrategy() {
        final GeneratorStrategy wrapper = super.getStrategy();
        if (WingsCodeGenConf.notGlobalSuffix()) {
            return wrapper;
        }

        if (proxyStrategy == null) {
            final String[] defs = {"DefaultCatalog", "DefaultSchema"};
            InvocationHandler han = (ignoredProxy, method, args) -> {
                Object obj = method.invoke(wrapper, args);
                final String methodName = method.getName();
                if ("getJavaClassName".equals(methodName) || "getFullJavaIdentifier".equals(methodName)) {
                    final Object arg = args[0];
                    if (arg instanceof CatalogDefinition || arg instanceof SchemaDefinition) {
                        return WingsCodeGenConf.tryGlobalSuffix((String) obj, defs);
                    }
                }
                if ("getFile".equals(methodName)) {
                    final Object arg = args[0];
                    if (arg instanceof CatalogDefinition || arg instanceof SchemaDefinition) {
                        return WingsCodeGenConf.tryGlobalSuffix((File) obj, defs);
                    }
                }
                return obj;
            };

            proxyStrategy = (GeneratorStrategy) Proxy.newProxyInstance(wrapper.getClass().getClassLoader(), new Class[]{GeneratorStrategy.class}, han);
        }

        return proxyStrategy;
    }

    @Override // No confirmation required, comparable
    public void printSingletonInstance(JavaWriter out, Definition definition) {
        super.printSingletonInstance(out, definition);
        // 🦁>>>
        // table is TableDefinition : SysCommitJournalTable, SysCommitJournal
        final String className = getStrategy().getJavaClassName(definition);
        final String identifier = getStrategy().getJavaIdentifier(definition);
        var aliasName = genAlias(identifier); // N6
        var aliasLower = "pro.fessional.wings.faceless.database.jooq.WingsJooqEnv.uniqueAlias()"; // n6
        // public static final SysCommitJournalTable asN6 = SysCommitJournal.as(WingsJooqEnv.uniqueRuntimeAlias());
        out.println("public static final %s %s = %s.as(%s);", className, aliasName, identifier, aliasLower);
        // 🦁<<<
    }

    @Override // No confirmation needed, parent method is empty
    public void generateTableClassFooter(TableDefinition table, JavaWriter out) {
        // 🦁>>>
        // table is TableDefinition : SysCommitJournalTable, SysCommitJournal
        final String className = getStrategy().getJavaClassName(table);
        final String identifier = getStrategy().getJavaIdentifier(table);
        var aliasName = genAlias(identifier); // N6

        out.ref(NotNull.class);
        final List<ColumnDefinition> columns = table.getColumns();
        if (columns.stream().anyMatch(WingsJooqGenHelper.LightIdAware)) {
            out.javadoc("LightIdAware seqName");
            out.println("@Override");
            out.println("@NotNull");
            out.println("public String getSeqName() {");

            final Function<TableDefinition, String> fun = WingsJooqGenHelper.funSeqName.get();
            String seqName = fun != null ? fun.apply(table) : table.getOutputName();

            out.println("return \"%s\";", seqName);
            out.println("}");
        }

        out.println("");
        out.javadoc("alias %s", aliasName);
        out.println("@Override");
        out.println("@NotNull");
        out.println("public %s getAliasTable() {", className);
        out.println("return %s;", aliasName);
        out.println("}");

        var logicCol = columns.stream().filter(it -> {
            var col = it.getOutputName();
            return col.equalsIgnoreCase(COL_DELETE_DT) || col.equalsIgnoreCase(COL_IS_DELETED);
        }).findFirst();

        if (logicCol.isPresent()) {
            ColumnDefinition colDel = logicCol.get();
            out.ref(Condition.class);
            out.ref(EmptyValue.class);
            out.ref(Map.class);
            out.ref(HashMap.class);
            out.ref(JournalService.class);
            var fldDel = reflectMethodRef(out, getStrategy().getJavaIdentifier(colDel), colRefSegments(colDel));

            var namDel = colDel.getOutputName();
            out.println("");
            out.javadoc("The colDel <code>%s</code> condition", namDel);
            final String markDelete;
            if (namDel.equalsIgnoreCase(COL_DELETE_DT)) {
                var colType = colDel.getDefinedType().getType().toLowerCase();
                if (colType.contains("time")) {
                    markDelete = "commit.getCommitDt()";
                    if (WingsCodeGenConf.isLiveDataByMax()) {
                        out.println("public final Condition DiedDataCondition = %s.gt(EmptyValue.DATE_TIME_AS_MAX);", fldDel);
                        out.println("public final Condition LiveDataCondition = %s.lt(EmptyValue.DATE_TIME_AS_MAX);", fldDel);
                    }
                    else {
                        out.println("public final Condition DiedDataCondition = %s.gt(EmptyValue.DATE_TIME);", fldDel);
                        out.println("public final Condition LiveDataCondition = %s.eq(EmptyValue.DATE_TIME);", fldDel);
                    }
                }
                else if (colType.contains("int")) {
                    markDelete = "DateNumber.dateTime17(commit.getCommitDt())";
                    out.ref(DateNumber.class);
                    out.println("public final Condition DiedDataCondition = %s.gt(EmptyValue.BIGINT);", fldDel);
                    out.println("public final Condition LiveDataCondition = %s.eq(EmptyValue.BIGINT);", fldDel);
                }
                else {
                    markDelete = "DateFormatter.full23(commit.getCommitDt())";
                    out.ref(DateFormatter.class);
                    out.println("public final Condition DiedDataCondition = %s.gt(EmptyValue.VARCHAR);", fldDel);
                    out.println("public final Condition LiveDataCondition = %s.eq(EmptyValue.VARCHAR);", fldDel);
                }
            }
            else {
                // COL_IS_DELETED
                markDelete = "Boolean.TRUE";
                out.println("public final Condition DiedDataCondition = %s.eq(Boolean.TRUE);", fldDel);
                out.println("public final Condition LiveDataCondition = %s.eq(Boolean.FALSE);", fldDel);
            }

            out.println("");
            out.println("@Override");
            out.println("@NotNull");
            out.println("public Condition getOnlyDied() {");
            out.println("return DiedDataCondition;");
            out.println("}");

            out.println("");
            out.println("@Override");
            out.println("@NotNull");
            out.println("public Condition getOnlyLive() {");
            out.println("return LiveDataCondition;");
            out.println("}");

            out.println("");
            out.println("@Override");
            out.println("@NotNull");
            out.println("public Map<Field<?>, ?> markDelete(JournalService.Journal commit) {");
            out.println("Map<org.jooq.Field<?>, Object> map = new HashMap<>();");
            out.println("map.put(%s, %s);", fldDel, markDelete);

            var commitCol = columns.stream().filter(it ->
                    it.getOutputName().equalsIgnoreCase(COL_COMMIT_ID)).findFirst();
            if (commitCol.isPresent()) {
                final ColumnDefinition colCid = commitCol.get();
                var fldCid = reflectMethodRef(out, getStrategy().getJavaIdentifier(colCid), colRefSegments(colCid));
                out.println("map.put(%s, commit.getCommitId());", fldCid);
            }
            out.println("return map;");
            out.println("}");
        }

        // import
        Set<String> import4Table = WingsCodeGenConf.getImport4Table();
        if (!import4Table.isEmpty()) {
            StringBuilder java = reflectFieldSb(out);
            final Set<String> qts = reflectFieldQt(out);
            String str = java.toString();
            boolean got = false;
            for (String imp : import4Table) {
                int p = imp.lastIndexOf('.');
                if (p > 0 && str.contains(imp)) { // avoid useless import, check first
                    String rep = imp.substring(p + 1);
                    str = str.replace(imp, rep);
                    qts.add(imp);
                    got = true;
                }
            }

            if (got) {
                java.setLength(0);
                java.append(str);
            }
        }

        // 🦁<<<
    }

    @Override // Confirm the replacement code and diff it
    public void generateDao(TableDefinition table, JavaWriter out) {
        super.generateDao(table, out);
        if (generateSpringAnnotations()) {
            out.ref(ConditionalWingsEnabled.class);
        }
        // 🦁>>>
        final Class<?> implClass;
        if (table.getColumns().stream().anyMatch(WingsJooqGenHelper.JournalAware)) {
            implClass = WingsJooqDaoJournalImpl.class;
        }
        else {
            implClass = WingsJooqDaoAliasImpl.class;
        }

        final Set<String> imports = reflectFieldQt(out);
        imports.remove(DAOImpl.class.getName());
        imports.add(implClass.getName());
        imports.add(Collection.class.getName());

        final StringBuilder java = reflectFieldSb(out);
        WingsJooqGenHelper.replaceDaoJava(java, implClass);
        // 🦁<<<
    }


    /////////////////

    private String genAlias(String id) {
        final String chr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var ix = id.hashCode() % chr.length();
        var cd = ix < 0 ? chr.charAt(-ix) : chr.charAt(ix);
        var sq = id.length() % 10;
        return ("as" + cd) + sq;
    }

    private int colRefSegments(TypedElementDefinition<?> column) {
        if (column != null && column.getContainer() instanceof UDTDefinition)
            return 2;

        if (!getStrategy().getInstanceFields())
            return 2;

        return 3;
    }

    private final Map<Class<?>, Method> methodRef = new HashMap<>();

    private String reflectMethodRef(JavaWriter out, String str, int kep) {
        final Method md = methodRef.computeIfAbsent(out.getClass(), key -> {
            Class<?> clz = key;
            while (clz != null) {
                try {
                    Method ref = clz.getDeclaredMethod("ref", String.class, int.class);
                    ref.setAccessible(true);
                    return ref;
                }
                catch (Exception e) {
                    clz = clz.getSuperclass();
                }
            }
            throw new IllegalStateException("can not get ref method");
        });

        try {
            Object rst = md.invoke(out, str, kep);
            return (String) rst;
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private final Map<Class<?>, Field> fieldQt = new HashMap<>();

    @SuppressWarnings("unchecked")
    private Set<String> reflectFieldQt(JavaWriter out) {

        final Field fd = fieldQt.computeIfAbsent(out.getClass(), key -> {
            Class<?> clz = key;
            while (clz != null) {
                try {
                    Field fld = clz.getDeclaredField("qualifiedTypes");
                    fld.setAccessible(true);
                    return fld;
                }
                catch (Exception e) {
                    clz = clz.getSuperclass();
                }
            }
            throw new IllegalStateException("can not get ref method");
        });

        try {
            return (Set<String>) fd.get(out);
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private final Map<Class<?>, Field> fieldSb = new HashMap<>();

    private StringBuilder reflectFieldSb(JavaWriter out) {
        final Field fd = fieldSb.computeIfAbsent(out.getClass(), key -> {
            Class<?> clz = key;
            while (clz != null) {
                try {
                    Field fld = clz.getDeclaredField("sb");
                    fld.setAccessible(true);
                    return fld;
                }
                catch (Exception e) {
                    clz = clz.getSuperclass();
                }
            }
            throw new IllegalStateException("can not get ref method");
        });
        try {
            return (StringBuilder) fd.get(out);
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}

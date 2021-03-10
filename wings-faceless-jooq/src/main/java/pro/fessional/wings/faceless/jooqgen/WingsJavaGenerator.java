package pro.fessional.wings.faceless.jooqgen;

import lombok.val;
import org.jooq.Condition;
import org.jooq.codegen.JavaGenerator;
import org.jooq.codegen.JavaWriter;
import org.jooq.meta.ColumnDefinition;
import org.jooq.meta.Definition;
import org.jooq.meta.TableDefinition;
import org.jooq.meta.TypedElementDefinition;
import org.jooq.meta.UDTDefinition;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.database.helper.JournalJdbcHelp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WingsJavaGenerator extends JavaGenerator {

    @Override
    public void printSingletonInstance(JavaWriter out, Definition definition) {
        // table is TableDefinition : SysCommitJournalTable, SysCommitJournal
        final String className = getStrategy().getJavaClassName(definition);
        final String identifier = getStrategy().getJavaIdentifier(definition);

        // 🦁>>>
        val aliasName = genAlias(identifier); // N6
        val aliasLower = aliasName.toLowerCase(); // n6
        // 🦁<<<

        out.javadoc("The reference instance of <code>%s</code>", definition.getQualifiedOutputName());

        // public static final SysCommitJournalTable SysCommitJournal = new SysCommitJournalTable();
        out.println("public static final %s %s = new %s();", className, identifier, className);

        // 🦁>>>
        // public static final SysCommitJournalTable asN6 = SysCommitJournal.as("n6");
        out.tab(1).println("public static final %s as%s = %s.as(\"%s\");", className, aliasName, identifier, aliasLower);
        // 🦁<<<
    }

    private static final Set<String> import4Table = new HashSet<>();

    public static void shortImport4Table(String claz) {
        if (claz == null || claz.isEmpty()) return;
        import4Table.add(claz);
    }

    @Override
    public void generateTableClassFooter(TableDefinition table, JavaWriter out) {
        // 🦁>>>
        // table is TableDefinition : SysCommitJournalTable, SysCommitJournal
        final String className = getStrategy().getJavaClassName(table);
        final String identifier = getStrategy().getJavaIdentifier(table);
        val aliasName = genAlias(identifier); // N6

        out.javadoc("alias %s", aliasName);
        out.println("@Override");
        out.println("public %s getAliasTable() {", className);
        out.println("    return as%s;", aliasName);
        out.println("}");

        if (table.getColumns().stream().anyMatch(WingsJooqGenHelp.LightIdAware)) {
            out.javadoc("LightIdAware seqName");
            out.println("@Override");
            out.println("public String getSeqName() {");

            final Function<TableDefinition, String> fun = WingsJooqGenHelp.funSeqName.get();
            String seqName = fun != null ? fun.apply(table) : table.getOutputName();

            out.println("    return \"%s\";", seqName);
            out.println("}");
        }

        val logicCol = table.getColumns().stream().filter(it -> {
            val col = it.getOutputName();
            return col.equalsIgnoreCase(JournalJdbcHelp.COL_DELETE_DT) || col.equalsIgnoreCase(JournalJdbcHelp.COL_IS_DELETED);
        }).findFirst();

        if (logicCol.isPresent()) {
            ColumnDefinition column = logicCol.get();
            out.ref(Condition.class);
            out.ref(EmptyValue.class);
            val columnId = reflectMethodRef(out, getStrategy().getJavaIdentifier(column), colRefSegments(column));

            val col = column.getOutputName();
            out.javadoc("The column <code>%s</code> condition", col);
            if (col.equalsIgnoreCase(JournalJdbcHelp.COL_DELETE_DT)) {
                val colType = column.getDefinedType().getType().toLowerCase();
                if (colType.startsWith("datetime")) {
                    out.println("public final Condition onlyDiedData = %s.gt(EmptyValue.DATE_TIME);", columnId);
                    out.println("public final Condition onlyLiveData = %s.eq(EmptyValue.DATE_TIME);", columnId);
                } else if (colType.startsWith("bigint")) {
                    out.println("public final Condition onlyDiedData = %s.gt(EmptyValue.BIGINT);", columnId);
                    out.println("public final Condition onlyLiveData = %s.eq(EmptyValue.BIGINT);", columnId);
                } else {
                    out.println("public final Condition onlyDiedData = %s.gt(EmptyValue.VARCHAR);", columnId);
                    out.println("public final Condition onlyLiveData = %s.eq(EmptyValue.VARCHAR);", columnId);
                }
            } else {
                // COL_IS_DELETED
                out.println("public final Condition onlyDiedData = %s.eq(Boolean.TRUE);", columnId);
                out.println("public final Condition onlyLiveData = %s.eq(Boolean.FALSE);", columnId);
            }

            out.println("@Override");
            out.println("public Condition getOnlyDied() {");
            out.println("    return onlyDiedData;");
            out.println("}");
            out.println("@Override");
            out.println("public Condition getOnlyLive() {");
            out.println("    return onlyLiveData;");
            out.println("}");
        }

        // 缩短import
        if (!import4Table.isEmpty()) {
            StringBuilder java = reflectFieldSb(out);
            final Set<String> qts = reflectFieldQt(out);
            String str = java.toString();
            boolean got = false;
            for (String imp : import4Table) {
                int p = imp.lastIndexOf('.');
                if (p > 0 && str.contains(imp)) { // 避免import无用，先判断
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

    private final Pattern daoExtends = Pattern.compile("public class (\\S+)Dao extends (DAOImpl<)");

    @Override
    public void generateDao(TableDefinition table, JavaWriter out) {
        super.generateDao(table, out);

        Set<String> impt = reflectFieldQt(out);
        impt.remove("org.jooq.impl.DAOImpl");
        impt.add("pro.fessional.wings.faceless.database.jooq.WingsJooqDaoImpl");

        StringBuilder java = reflectFieldSb(out);
        String dao = java.toString();
        // "public class SysStandardI18nDao extends DAOImpl"
        Matcher m = daoExtends.matcher(dao);
        dao = m.replaceFirst("public class $1Dao extends WingsJooqDaoImpl<$1Table, ");

        java.setLength(0);
        java.append(dao);
    }
    /////////////////

    private final String chr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private String genAlias(String id) {
        val ix = id.hashCode() % chr.length();
        val cd = ix < 0 ? chr.charAt(-ix) : chr.charAt(ix);
        val sq = id.length() % 10;
        return cd + "" + sq;
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
                } catch (Exception e) {
                    clz = clz.getSuperclass();
                }
            }
            throw new IllegalStateException("can not get ref method");
        });

        try {
            Object rst = md.invoke(out, str, kep);
            return (String) rst;
        } catch (Exception e) {
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
                } catch (Exception e) {
                    clz = clz.getSuperclass();
                }
            }
            throw new IllegalStateException("can not get ref method");
        });

        try {
            return (Set<String>) fd.get(out);
        } catch (IllegalAccessException e) {
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
                } catch (Exception e) {
                    clz = clz.getSuperclass();
                }
            }
            throw new IllegalStateException("can not get ref method");
        });
        try {
            return (StringBuilder) fd.get(out);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}

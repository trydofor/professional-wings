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
import pro.fessional.wings.faceless.database.helper.JournalHelp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
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

        out.tab(1).javadoc("The reference instance of <code>%s</code>", definition.getQualifiedOutputName());

        // public static final SysCommitJournalTable SysCommitJournal = new SysCommitJournalTable();
        if (scala)
            out.tab(1).println("val %s = new %s", identifier, className);
        else
            out.tab(1).println("public static final %s %s = new %s();", className, identifier, className);

        // 🦁>>>
        // public static final SysCommitJournalTable asN6 = SysCommitJournal.as("n6");
        out.tab(1).println("public static final %s as%s = %s.as(\"%s\");", className, aliasName, identifier, aliasLower);
        // 🦁<<<
    }

    @Override
    public void generateTableClassFooter(TableDefinition table, JavaWriter out) {
        // 🦁>>>
        // table is TableDefinition : SysCommitJournalTable, SysCommitJournal
        final String className = getStrategy().getJavaClassName(table);
        final String identifier = getStrategy().getJavaIdentifier(table);
        val aliasName = genAlias(identifier); // N6

        out.tab(1).javadoc("alias %s", aliasName);
        out.tab(1).println("@Override");
        out.tab(1).println("public %s getAliasTable() {", className);
        out.tab(1).println("    return as%s;", aliasName);
        out.tab(1).println("}");

        val logicCol = table.getColumns().stream().filter(it -> {
            val col = it.getOutputName();
            return col.equalsIgnoreCase(JournalHelp.COL_DELETE_DT) || col.equalsIgnoreCase(JournalHelp.COL_IS_DELETED);
        }).findFirst();

        if (logicCol.isPresent()) {
            ColumnDefinition column = logicCol.get();
            out.ref(Condition.class);
            out.ref(EmptyValue.class);
            val columnId = reflectProtectRef(out, getStrategy().getJavaIdentifier(column), colRefSegments(column));

            val col = column.getOutputName();
            out.tab(1).javadoc("The column <code>%s</code> condition", col);
            if (col.equalsIgnoreCase(JournalHelp.COL_DELETE_DT)) {
                val colType = column.getDefinedType().getType().toLowerCase();
                if (colType.startsWith("datetime")) {
                    out.tab(1).println("public final Condition onlyDiedData = %s.gt(EmptyValue.DATE_TIME);", columnId);
                    out.tab(1).println("public final Condition onlyLiveData = %s.eq(EmptyValue.DATE_TIME);", columnId);
                } else if (colType.startsWith("bigint")) {
                    out.tab(1).println("public final Condition onlyDiedData = %s.gt(EmptyValue.BIGINT);", columnId);
                    out.tab(1).println("public final Condition onlyLiveData = %s.eq(EmptyValue.BIGINT);", columnId);
                } else {
                    out.tab(1).println("public final Condition onlyDiedData = %s.gt(EmptyValue.VARCHAR);", columnId);
                    out.tab(1).println("public final Condition onlyLiveData = %s.eq(EmptyValue.VARCHAR);", columnId);
                }
            } else {
                // COL_IS_DELETED
                out.tab(1).println("public final Condition onlyDiedData = %s.eq(Boolean.TRUE);", columnId);
                out.tab(1).println("public final Condition onlyLiveData = %s.eq(Boolean.FALSE);", columnId);
            }

            out.tab(1).println("@Override");
            out.tab(1).println("public Condition getOnlyDied() {");
            out.tab(1).println("    return onlyDiedData;");
            out.tab(1).println("}");
            out.tab(1).println("@Override");
            out.tab(1).println("public Condition getOnlyLive() {");
            out.tab(1).println("    return onlyLiveData;");
            out.tab(1).println("}");
        }
        // 🦁<<<
    }

    private final Pattern daoExtends = Pattern.compile("public class (\\S+)Dao extends (DAOImpl<)");

    @Override
    public void generateDao(TableDefinition table, JavaWriter out) {
        super.generateDao(table, out);

        try {
            Class<? extends JavaWriter> jwc = out.getClass();
            Field fldImpt = jwc.getDeclaredField("qualifiedTypes");
            fldImpt.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<String> impt = (Set<String>)fldImpt.get(out);
            impt.remove("org.jooq.impl.DAOImpl");
            impt.add("pro.fessional.wings.faceless.database.jooq.WingsJooqDaoImpl");

            Field fldJava = jwc.getSuperclass().getDeclaredField("sb");
            fldJava.setAccessible(true);
            StringBuilder java = (StringBuilder) fldJava.get(out);
            String dao = java.toString();
            // "public class SysStandardI18nDao extends DAOImpl"
            Matcher m = daoExtends.matcher(dao);
            dao = m.replaceFirst("public class $1Dao extends WingsJooqDaoImpl<$1Table, ");

            java.delete(0,java.length());
            java.append(dao);
        } catch (Exception e) {
            throw new RuntimeException("failed to replace to WingsJooqDaoImpl", e);
        }

        // replace to WingsJooqDaoImpl
    }
    /////////////////

    private final String chr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final boolean scala = false;

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

    private String reflectProtectRef(JavaWriter out, String str, int kep) {
        Class<? super JavaWriter> clz = JavaWriter.class;
        while (clz != null) {
            try {
                Method ref = clz.getDeclaredMethod("ref", String.class, int.class);
                //
                ref.setAccessible(true);
                Object rst = ref.invoke(out, str, kep);
                return (String) rst;
            } catch (Exception e) {
                clz = clz.getSuperclass();
            }
        }
        return str;
    }
}
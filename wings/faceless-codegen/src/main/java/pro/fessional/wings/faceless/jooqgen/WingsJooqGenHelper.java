package pro.fessional.wings.faceless.jooqgen;

import org.jooq.meta.ColumnDefinition;
import org.jooq.meta.TableDefinition;
import pro.fessional.wings.faceless.database.jooq.WingsJooqDaoJournalImpl;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_COMMIT_ID;
import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_CREATE_DT;
import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_DELETE_DT;
import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_IS_DELETED;
import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_MODIFY_DT;
import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_MODIFY_TM;

/**
 * @author trydofor
 * @since 2021-01-20
 */
public class WingsJooqGenHelper {

    public static final AtomicReference<Function<TableDefinition, String>> funSeqName = new AtomicReference<>();

    public static final Predicate<ColumnDefinition> JournalAware = it -> {
        String name = it.getName();
        int p = name.lastIndexOf(".");
        if (p > 0) {
            name = name.substring(p + 1);
        }

        return name.equalsIgnoreCase(COL_CREATE_DT)
               || name.equalsIgnoreCase(COL_MODIFY_DT)
               || name.equalsIgnoreCase(COL_MODIFY_TM)
               || name.equalsIgnoreCase(COL_DELETE_DT)
               || name.equalsIgnoreCase(COL_IS_DELETED)
               || name.equalsIgnoreCase(COL_COMMIT_ID);
    };

    public static final Predicate<ColumnDefinition> LightIdAware = it -> {
        String name = it.getName();
        int p = name.lastIndexOf(".");
        if (p > 0) {
            name = name.substring(p + 1);
        }

        return "id".equalsIgnoreCase(name) && it.getDefinedType().getType().toLowerCase().contains("bigint");
    };


    private static final Pattern daoExtends = Pattern.compile("public class (\\S+)Dao extends (DAOImpl<)");
    private static final Pattern daoFetchBody = Pattern.compile("\n[^\n]+ fetch[^(]+\\([^}]+\\}\n", Pattern.MULTILINE);
    private static final Pattern daoFetchLive = Pattern.compile("(fetch[^(]*)\\(");
    private static final Pattern daoFetchVars = Pattern.compile("\\(([^.]+)\\.\\.\\. values\\)");

    /**
     * org.jooq.codegen.JavaGenerator#generateDao(TableDefinition, JavaWriter)
     */
    public static void replaceDaoJava(StringBuilder java, Class<?> implClass) {

        final String tmp = java.toString();
        final Matcher me = daoExtends.matcher(tmp); // "public class SysStandardI18nDao extends DAOImpl<"
        final String dao = me.replaceFirst("public class $1Dao extends " + implClass.getSimpleName() + "<$1Table, ");

        // Reset the Content, Regexp Replacement
        java.setLength(0);
        int off = 0;

        final boolean jnl = implClass.equals(WingsJooqDaoJournalImpl.class);
        final Matcher md = daoFetchBody.matcher(dao);
        while (md.find()) {
            java.append(dao, off, md.start());
            off = md.end();

            String body = md.group();
            // vararg and Collection
            Matcher mr = daoFetchVars.matcher(body);
            if (mr.find()) {
                String type = mr.group(1);
                String vrg = mr.replaceAll("(Collection<? extends $1> values)");
                int p0 = vrg.indexOf("[values.length];"); // 16 chars
                if (p0 > 0) {
                    int p1 = p0 + 16;
                    int p2 = vrg.indexOf("for", p1);
                    String tab = vrg.substring(p1, p2);
                    vrg = vrg.replace("for (int i = 0; i < values.length; i++)", "int i = 0;" + tab + "for (" + type + " el : values)")
                             .replace("values.length", "values.size()")
                             .replace("records[i]", "records[i++]")
                             .replace("values[i]", "el");
                }
                body = body + vrg;
            }
            java.append(body);

            // Live for journal
            if (jnl) {
                Matcher ml = daoFetchLive.matcher(body);
                String live = ml.replaceAll("$1Live(");
                java.append("\n");
                java.append(live);
            }
        }

        if (java.isEmpty()) {
            java.append(dao);
        }
        else {
            java.append(dao.substring(dao.lastIndexOf("}")));
        }
    }
}

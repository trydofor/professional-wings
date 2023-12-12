package pro.fessional.wings.faceless.database.jooq.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Delete;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.conf.ParamType;
import pro.fessional.wings.faceless.database.jooq.helper.JournalJooqHelper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * Only supports single table execution, does not support batch processing
 *
 * delete from `tst_sharding` where (`id` = ? and `commit_id` = ?)
 * commit_id = :commit_id and `id` = ?
 * </pre>
 *
 * @author trydofor
 * @since 2021-01-14
 */
@Slf4j
public class JournalDeleteListener implements ExecuteListener {

    @Override
    public void renderEnd(ExecuteContext ctx) {
        Query query = ctx.query();
        if (!(query instanceof Delete)) {
            return;
        }

        final String sql = ctx.sql();
        if (sql == null) return;

        if (notJournalDelete(sql)) return;

        String table = parseTable(sql);
        if (table == null) return;

        Map<String, Param<?>> params = query.getParams();
        if (!params.isEmpty()) {
            for (Param<?> value : params.values()) {
                ParamType type = value.getParamType();
                // only handle indexed
                if (!(type == ParamType.INDEXED || type == ParamType.FORCE_INDEXED)) {
                    return;
                }
            }
            params = new LinkedHashMap<>(params);
        }
        String updateSql = buildUpdateSql(ctx.dsl(), sql, table, params);
        if (updateSql == null) return;

        log.debug("Wings journal-delete, sql={}", updateSql);

        try {
            if (params.isEmpty()) {
                ctx.dsl().execute(updateSql);
            }
            else {
                // make sure the order
                Object[] pms = new Object[params.size()];
                int i = 0;
                for (Param<?> pm : params.values()) {
                    pms[i++] = pm.getValue();
                }

                ctx.dsl().execute(updateSql, pms);
            }
        }
        catch (Exception e) {
            log.error(updateSql, e);
        }
    }

    /**
     * DELETE [LOW_PRIORITY] [QUICK] [IGNORE] FROM tbl_name
     * [PARTITION (partition_name [, partition_name] ...)]
     * [WHERE where_condition]
     */
    private String parseTable(String sql) {
        int fi = StringUtils.indexOfIgnoreCase(sql, "from");
        if (fi < 0) return null;
        int wi = StringUtils.indexOfIgnoreCase(sql, "where", fi + 5);

        char quote = 0;
        int bgn = -1;
        for (int i = fi + 4; i < wi; i++) {
            char c = sql.charAt(i);
            if (c == '`' || c == '"' || c == '\'') {
                if (quote == 0) {
                    quote = c;
                    bgn = i;
                    continue;
                }
                else if (quote == c) {
                    return sql.substring(bgn, i + 1);
                }
            }
            if (c > ' ') {
                if (bgn < 0) bgn = i;
            }
            else {
                if (quote == 0 && bgn > 0) {
                    return sql.substring(bgn, i);
                }
            }
        }
        return null;
    }

    // delete from `tst_sharding` where (`id` = ? and `commit_id` = ?)
    // commit_id = :commit_id and `id` = ?
    private final Pattern ptnCommitId = Pattern
            .compile("\\band\\s+([`'\"]?commit_id[`'\"]?\\s*=\\s*([^()=\\s]+))" +
                     "|" +
                     "([`'\"]?commit_id[`'\"]?\\s*=\\s*([^()=\\s]+))\\s+and\\b"
                    , Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private String buildUpdateSql(DSLContext dsl, String del, String table, Map<String, Param<?>> params) {
        Matcher matcher = ptnCommitId.matcher(del);
        if (!matcher.find()) return null;
        String cidWhere = matcher.group();

        int off = 0;
        String cidSql = matcher.group(1);
        if (cidSql == null) {
            off = 2;
            cidSql = matcher.group(3);
        }

        if (!params.isEmpty()) {
            String cidVal = matcher.group(off + 2).trim();
            Param<?> para;
            if ("?".equals(cidVal)) {
                int cn = 1;
                for (int i = 0; i < matcher.start(); i++) {
                    char c = del.charAt(i);
                    if (c == '?') cn++;
                }
                para = params.remove(String.valueOf(cn));
            }
            else {
                para = params.remove(cidVal);
            }

            if (para == null) {
                cidVal = cidVal.trim();
                for (int i = 0; i < cidVal.length(); i++) {
                    char c = cidVal.charAt(i);
                    if (!(c == '-' || (c >= '0' && c <= '9'))) return null;
                }
            }
            else {
                cidSql = cidSql.replace(cidVal, String.valueOf(para.getValue()));
            }
        }

        StringBuilder upd = new StringBuilder("UPDATE ");
        upd.append(table);
        upd.append(" SET ");
        upd.append(cidSql);
        upd.append(' ');
        String jf = JournalJooqHelper.getJournalDateColumn(dsl, table);
        if (!jf.isEmpty()) {
            upd.append(',').append(jf).append(" = NOW(3) ");
        }

        String where = del.substring(StringUtils.indexOfIgnoreCase(del, "where"));
        upd.append(where.replace(cidWhere, ""));
        return upd.toString();
    }

    private boolean notJournalDelete(String sql) {
        int di = StringUtils.indexOfIgnoreCase(sql, "delete");
        if (di < 0) return true;

        int ci = StringUtils.indexOfIgnoreCase(sql, "commit_id", di + 7);
        return ci < di;
    }
}

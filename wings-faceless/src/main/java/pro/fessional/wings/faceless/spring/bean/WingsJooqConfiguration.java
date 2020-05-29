package pro.fessional.wings.faceless.spring.bean;

import org.jooq.Clause;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListenerProvider;
import org.jooq.Insert;
import org.jooq.Merge;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.Update;
import org.jooq.VisitContext;
import org.jooq.VisitListenerProvider;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.jooq.impl.DefaultVisitListener;
import org.jooq.impl.DefaultVisitListenerProvider;
import org.jooq.impl.TableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import pro.fessional.wings.faceless.database.helper.JournalHelp;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2019-08-12
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.wings.jooq", name = "enabled", havingValue = "true")
public class WingsJooqConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(WingsJooqConfiguration.class);

    @Bean
    @Order
    @ConditionalOnMissingBean(Settings.class)
    public Settings settings() {
        // ObjectProvider<Settings> settings
        return new Settings()
                .withRenderCatalog(false)
                .withRenderSchema(false)
                .withParseDialect(SQLDialect.MYSQL)
//                .withRenderTable(false)
                ;
    }


    /**
     * workaround before Version 3.14.0
     *
     * @link https://github.com/jOOQ/jOOQ/issues/8893
     * @link https://github.com/jOOQ/jOOQ/issues/9055
     * @link https://github.com/jOOQ/jOOQ/issues/7258
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.wings.jooq.auto-qualify", name = "enabled", havingValue = "true")
    public VisitListenerProvider autoQualifyFieldListener() {
        return new DefaultVisitListenerProvider(new DefaultVisitListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void visitStart(VisitContext context) {
                if (!(context.queryPart() instanceof TableField)) return;

                TableField<?, ?> field = (TableField<?, ?>) context.queryPart();
                Table<?> table = field.getTable();
                if (!(table instanceof TableImpl)) return;

                boolean notAlias = true;
                for (Clause clause : ((TableImpl<?>) table).clauses(context.context())) {
                    if (clause == Clause.TABLE_ALIAS) {
                        notAlias = false;
                        break;
                    }
                }

                if (notAlias) {
                    context.queryPart(DSL.field(field.getUnqualifiedName(), field.getDataType()));
                }
            }
        });
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.wings.trigger.journal-delete", name = "enabled", havingValue = "true")
    public ExecuteListenerProvider journalDeleteListener() {
        return new DefaultExecuteListenerProvider(new DefaultExecuteListener() {

            @Override
            public void renderEnd(ExecuteContext ctx) {
                Query query = ctx.query();
                if (query == null
                        || query instanceof Update
                        || query instanceof Insert
                        || query instanceof Merge) {
                    return;
                }

                final String sql = ctx.sql();
                final String low = sql.toLowerCase();
                if (notJournalDelete(low)) return;

                String table = parseTable(sql, low);
                if (table == null) return;

                Map<String, Param<?>> params = query.getParams();
                if (!params.isEmpty()) {
                    for (Param<?> value : params.values()) {
                        ParamType type = value.getParamType();
                        // 只处理indexed的
                        if (!(type == ParamType.INDEXED || type == ParamType.FORCE_INDEXED)) {
                            return;
                        }
                    }
                    params = new LinkedHashMap<>(params);
                }
                String updateSql = buildUpdateSql(ctx.dsl(), sql, low, table, params);
                if (updateSql == null) return;

                logger.info("Wings journal-delete, sql={}", updateSql);

                try {
                    if (params.isEmpty()) {
                        ctx.dsl().execute(updateSql);
                    } else {
                        // 保证顺序
                        Object[] pms = new Object[params.size()];
                        int i = 0;
                        for (Param<?> pm : params.values()) {
                            pms[i++] = pm.getValue();
                        }

                        ctx.dsl().execute(updateSql, pms);
                    }
                } catch (Exception e) {
                    logger.error(updateSql, e);
                }
            }

            /**
             DELETE [LOW_PRIORITY] [QUICK] [IGNORE] FROM tbl_name
             [PARTITION (partition_name [, partition_name] ...)]
             [WHERE where_condition]
             */
            private String parseTable(String sql, String low) {
                int fi = low.indexOf("from");
                if (fi < 0) return null;
                int wi = low.indexOf("where");

                char quote = 0;
                int bgn = -1;
                for (int i = fi + 4; i < wi; i++) {
                    char c = low.charAt(i);
                    if (c == '`' || c == '"' || c == '\'') {
                        if (quote == 0) {
                            quote = c;
                            bgn = i;
                            continue;
                        } else if (quote == c) {
                            return sql.substring(bgn, i + 1);
                        }
                    }
                    if (c > ' ') {
                        if (bgn < 0) bgn = i;
                    } else {
                        if (quote == 0 && bgn > 0) {
                            return sql.substring(bgn, i);
                        }
                    }
                }
                return null;
            }

            // delete from `tst_中文也分表` where (`id` = ? and `commit_id` = ?)
            // commit_id = :commit_id and `id` = ?
            private final Pattern ptnCommitId = Pattern.compile("" +
                            "\\band\\s+([`'\"]?commit_id[`'\"]?[\\s]*=[\\s]*([^()=\\s]+))" +
                            "|" +
                            "([`'\"]?commit_id[`'\"]?[\\s]*=[\\s]*([^()=\\s]+))\\s+and\\b"
                    , Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

            private String buildUpdateSql(DSLContext dsl, String deleteSql, String lower, String table, Map<String, Param<?>> params) {
                Matcher matcher = ptnCommitId.matcher(deleteSql);
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
                    if (cidVal.equals("?")) {
                        int cn = 1;
                        for (int i = 0; i < matcher.start(); i++) {
                            char c = deleteSql.charAt(i);
                            if (c == '?') cn++;
                        }
                        para = params.remove(String.valueOf(cn));
                    } else {
                        para = params.remove(cidVal);
                    }

                    if (para == null) {
                        cidVal = cidVal.trim();
                        for (int i = 0; i < cidVal.length(); i++) {
                            char c = cidVal.charAt(i);
                            if (!(c == '-' || (c >= '0' && c <= '9'))) return null;
                        }
                    } else {
                        cidSql = cidSql.replace(cidVal, String.valueOf(para.getValue()));
                    }
                }

                StringBuilder sql = new StringBuilder("UPDATE ");
                sql.append(table);
                sql.append(" SET ");
                sql.append(cidSql);
                sql.append(" ");
                String jf = JournalHelp.getJournalColumn(dsl, table);
                if (!jf.isEmpty()) {
                    sql.append(",").append(jf).append(" = NOW() ");
                }
                String where = deleteSql.substring(lower.indexOf("where"));
                sql.append(where.replace(cidWhere, ""));
                return sql.toString();
            }

            private boolean notJournalDelete(String low) {
                int di = low.indexOf("delete");
                if (di < 0) return true;

                int ci = low.indexOf("commit_id");
                return ci < di;
            }
        });
    }
}

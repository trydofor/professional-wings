package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring-wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(FacelessJooqEnabledProp.Key)
public class FacelessJooqEnabledProp {

    public static final String Key = "spring.wings.faceless.jooq.enabled";

    /**
     * 是否开启jooq配置
     *
     * @see #Key$module
     */
    private boolean module = true;
    public static final String Key$module = Key + ".module";

    /**
     * 自动配置table限定，无alias时不使用
     *
     * @see #Key$autoQualify
     */
    private boolean autoQualify = true;
    public static final String Key$autoQualify = Key + ".auto-qualify";

    /**
     * 执行dao的批量插入时，使用高效的mysql语法
     *
     * @see #Key$batchMysql
     */
    private boolean batchMysql = true;
    public static final String Key$batchMysql = Key + ".batch-mysql";

    /**
     * 是否注入全局converter，如table中注入了，外部可以不用注入
     *
     * @see #Key$converter
     */
    private boolean converter = false;
    public static final String Key$converter = Key + ".converter";

    /**
     * db执行delete且有commit_id时，先执行update再delete
     *
     * @see #Key$journalDelete
     */
    private boolean journalDelete = false;
    public static final String Key$journalDelete = Key + ".journal-delete";

    /**
     * 是否监听table的create,update,delete
     *
     * @see #Key$listenTableCud
     */
    private boolean listenTableCud = false;
    public static final String Key$listenTableCud = Key + ".listen-table-cud";
}

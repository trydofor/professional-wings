package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @see #Key
 * @since 2019-05-30
 */
@Data
@ConfigurationProperties(FlywaveSqlProp.Key)
public class FlywaveSqlProp {

    public static final String Key = "wings.faceless.flywave.sql";

    /**
     * sql类型，当前只支持"mysql"
     *
     * @see #Key$dialect
     */
    private String dialect = "mysql";
    public static final String Key$dialect = Key + ".dialect";

    /**
     * 原始分隔符，必须存在，默认";"。
     *
     * @see #Key$delimiterDefault
     */
    private String delimiterDefault = ";";
    public static final String Key$delimiterDefault = Key + ".delimiter-default";

    /**
     * 重定义的分隔符的命令，默认"DELIMITER"
     *
     * @see #Key$delimiterCommand
     */
    private String delimiterCommand = "DELIMITER";
    public static final String Key$delimiterCommand = Key + ".delimiter-command";

    /**
     * 单行注释，默认 "--"
     *
     * @see #Key$commentSingle
     */
    private String commentSingle = "--";
    public static final String Key$commentSingle = Key + ".comment-single";

    /**
     * 多行注释，开头和结束以空格分开表示
     *
     * @see #Key$commentMultiple
     */
    private String commentMultiple = "/*   */";
    public static final String Key$commentMultiple = Key + ".comment-multiple";

    /**
     * 设置分表格式
     *
     * @see #Key$formatShard
     */
    private String formatShard = "";
    public static final String Key$formatShard = Key + ".format-shard";

    /**
     * 设置跟踪表格式
     *
     * @see #Key$formatTrace
     */
    private String formatTrace = "";
    public static final String Key$formatTrace = Key + ".format-trace";
}

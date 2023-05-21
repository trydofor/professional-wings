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
     * sql dialect, currently only supports `mysql`.
     *
     * @see #Key$dialect
     */
    private String dialect = "mysql";
    public static final String Key$dialect = Key + ".dialect";

    /**
     * the original delimiter, required.
     *
     * @see #Key$delimiterDefault
     */
    private String delimiterDefault = ";";
    public static final String Key$delimiterDefault = Key + ".delimiter-default";

    /**
     * the command to redefine the delimiter.
     *
     * @see #Key$delimiterCommand
     */
    private String delimiterCommand = "DELIMITER";
    public static final String Key$delimiterCommand = Key + ".delimiter-command";

    /**
     * single line comment
     *
     * @see #Key$commentSingle
     */
    private String commentSingle = "--";
    public static final String Key$commentSingle = Key + ".comment-single";

    /**
     * multi-line comments, start and end with a space
     *
     * @see #Key$commentMultiple
     */
    private String commentMultiple = "/*   */";
    public static final String Key$commentMultiple = Key + ".comment-multiple";

    /**
     * set the shard table format. see SqlSegmentProcessor.setShardFormat.
     *
     * @see #Key$formatShard
     */
    private String formatShard = "";
    public static final String Key$formatShard = Key + ".format-shard";

    /**
     * set the trace table format. see SqlSegmentProcessor.setTraceFormat
     *
     * @see #Key$formatTrace
     */
    private String formatTrace = "";
    public static final String Key$formatTrace = Key + ".format-trace";
}

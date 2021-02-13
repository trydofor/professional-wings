package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @since 2019-05-30
 */
@Data
@ConfigurationProperties("wings.faceless.flywave.sql")
public class FlywaveSqlProp {
    /**
     * sql类型，当前只支持"mysql"
     */
    private String dialect = "mysql";
    /**
     * 原始分隔符，必须存在，默认";"。
     */
    private String delimiterDefault = ";";
    /**
     * 重定义的分隔符的命令，默认"DELIMITER"
     */
    private String delimiterCommand = "DELIMITER";
    /**
     * 单行注释，默认 "--"
     */
    private String commentSingle = "--";
    /**
     * 多行注释，开头和结束以空格分开表示
     */
    private String commentMultiple = "/*   */";
}

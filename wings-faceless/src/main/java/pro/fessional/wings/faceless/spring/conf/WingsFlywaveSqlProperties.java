package pro.fessional.wings.faceless.spring.conf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author trydofor
 * @since 2019-05-30
 */
@Component
@ConfigurationProperties("wings.flywave.sql")
@ConditionalOnProperty(prefix = "wings.flywave", name = "enabled", havingValue = "true", matchIfMissing = false)
public class WingsFlywaveSqlProperties {
    private String dialect = "";
    private String delimiterDefault = "";
    private String delimiterCommand = "";
    private String commentSingle = "";
    private String commentMultiple = "";

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getDelimiterDefault() {
        return delimiterDefault;
    }

    public void setDelimiterDefault(String delimiterDefault) {
        this.delimiterDefault = delimiterDefault;
    }

    public String getDelimiterCommand() {
        return delimiterCommand;
    }

    public void setDelimiterCommand(String delimiterCommand) {
        this.delimiterCommand = delimiterCommand;
    }

    public String getCommentSingle() {
        return commentSingle;
    }

    public void setCommentSingle(String commentSingle) {
        this.commentSingle = commentSingle;
    }

    public String getCommentMultiple() {
        return commentMultiple;
    }

    public void setCommentMultiple(String commentMultiple) {
        this.commentMultiple = commentMultiple;
    }
}

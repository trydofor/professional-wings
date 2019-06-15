package pro.fessional.wings.oracle.spring.conf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author trydofor
 * @since 2019-05-30
 */
@Component
@ConfigurationProperties("wings.flywave.ver")
@ConditionalOnProperty(prefix = "wings.flywave", name = "enabled", havingValue = "true", matchIfMissing = false)
public class WingsFlywaveVerProperties {
    private String journalUpdate = "";
    private String triggerUpdate = "";
    private String journalDelete = "";
    private String triggerDelete = "";

    public String getJournalUpdate() {
        return journalUpdate;
    }

    public void setJournalUpdate(String journalUpdate) {
        this.journalUpdate = journalUpdate;
    }

    public String getTriggerUpdate() {
        return triggerUpdate;
    }

    public void setTriggerUpdate(String triggerUpdate) {
        this.triggerUpdate = triggerUpdate;
    }

    public String getJournalDelete() {
        return journalDelete;
    }

    public void setJournalDelete(String journalDelete) {
        this.journalDelete = journalDelete;
    }

    public String getTriggerDelete() {
        return triggerDelete;
    }

    public void setTriggerDelete(String triggerDelete) {
        this.triggerDelete = triggerDelete;
    }
}

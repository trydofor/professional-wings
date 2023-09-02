package pro.fessional.wings.tiny.project;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaJournalManager;


/**
 * @author trydofor
 * @since 2021-02-22
 */
@SpringBootTest(properties = {
        "spring.wings.faceless.flywave.enabled.module=true",
        "spring.wings.faceless.flywave.enabled.checker=false",
        "wings.tiny.mail.service.boot-scan=0",
})
@Disabled("Code gen, managed by devops")
public class TinyMailCodeGenTest {
    @Setter(onMethod_ = {@Autowired})
    private SchemaJournalManager schemaJournalManager;

    @Test
    public void trigger() {
        schemaJournalManager.checkAndInitDdl("win_mail_sender", -1);
        schemaJournalManager.publishInsert("win_mail_sender", true, -1);
        schemaJournalManager.publishUpdate("win_mail_sender", true, -1);
    }
}

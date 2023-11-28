package pro.fessional.wings.tiny.project;

import io.qameta.allure.TmsLink;
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
        "wings.enabled.faceless.flywave=true",
        "wings.faceless.flywave.checker=false",
        "wings.tiny.mail.service.boot-scan=0",
})
@Disabled("Code gen, managed by devops")
public class TinyMailCodeGenTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaJournalManager schemaJournalManager;

    @Test
    @TmsLink("C15011")
    public void trigger() {
        schemaJournalManager.checkAndInitDdl("win_mail_sender", -1);
        schemaJournalManager.publishInsert("win_mail_sender", true, -1);
        schemaJournalManager.publishUpdate("win_mail_sender", true, -1);
    }
}

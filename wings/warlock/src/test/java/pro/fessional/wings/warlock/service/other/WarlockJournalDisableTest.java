package pro.fessional.wings.warlock.service.other;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.service.journal.JournalService;

/**
 * @author trydofor
 * @since 2023-01-25
 */
@SpringBootTest(properties = {
        "wings.enabled.faceless.simple-journal=true",
        "wings.enabled.pro.fessional.wings.warlock.spring.bean.WarlockJournalConfiguration.terminalJournalService=false"
})
class WarlockJournalDisableTest {

    @Setter(onMethod_ = {@Autowired})
    protected JournalService journalService;

    @Test
    @TmsLink("C14077")
    void terminalJournalService() {
        Assertions.assertFalse(journalService instanceof TerminalJournalService);
    }
}

package pro.fessional.wings.warlock.spring.bean;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.warlock.service.other.TerminalJournalService;

/**
 * @author trydofor
 * @since 2023-01-25
 */
@SpringBootTest
class WarlockJournalConfigurationTest {

    @Setter(onMethod_ = {@Autowired})
    protected JournalService journalService;

    @Test
    @TmsLink("C14066")
    void terminalJournalService() {
        Assertions.assertInstanceOf(TerminalJournalService.class, journalService);
    }
}

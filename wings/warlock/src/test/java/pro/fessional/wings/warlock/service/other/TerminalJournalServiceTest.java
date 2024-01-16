package pro.fessional.wings.warlock.service.other;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.service.journal.JournalService;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * @author trydofor
 * @since 2024-01-15
 */
@SpringBootTest(properties = {
        "wings.enabled.faceless.simple-journal=false",
})
@Slf4j
class TerminalJournalServiceTest {

    @Setter(onMethod_ = {@Autowired})
    protected JournalService journalService;

    @Test
    @TmsLink("C14079")
    void terminalJournalService() {
        Assertions.assertTrue(journalService instanceof TerminalJournalService);
        JournalService.Journal jnl = journalService.commit(TerminalJournalServiceTest.class, journal -> log.info("journal={}", journal));
        assertEquals(TerminalJournalServiceTest.class.getName(), jnl.getEventName());
    }
}
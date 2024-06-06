package pro.fessional.wings.warlock.database.jooq.converter;

import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.faceless.service.journal.JournalDiff;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author trydofor
 * @since 2022-10-25
 */
@Slf4j
class JooqJournalDiffConverterTest {

    @Test
    @TmsLink("C14001")
    void journalDiff() {
        JournalDiff d0 = new JournalDiff();
        d0.setTable("test_table");
        d0.setColumn(List.of("id", "name", "money", "now"));
        d0.setCount(1);
        BigDecimal pi = new BigDecimal("3.14");
        LocalDateTime ldt = LocalDateTime.of(2022, 10, 24, 12, 34, 56);
        d0.setValue1(List.of(10086L, "trydofor", pi, ldt));
        JooqJournalDiffConverter c = new JooqJournalDiffConverter();
        final String s = c.to(d0);
        log.warn("JournalDiff JSON ={}", s);
        // Type missing in list
        d0.setValue1(List.of(10086, "trydofor", pi, "2022-10-24 12:34:56"));
        final JournalDiff d1 = c.from(s);
        Assertions.assertEquals(d0, d1);
    }
}

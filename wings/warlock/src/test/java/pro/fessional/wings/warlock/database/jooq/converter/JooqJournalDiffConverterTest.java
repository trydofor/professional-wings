package pro.fessional.wings.warlock.database.jooq.converter;

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
    void to() {
        JournalDiff d0 = new JournalDiff();
        d0.setTable("test_table");
        d0.setColumn(List.of("id", "name", "money", "now"));
        d0.setCount(1);
        d0.setValue1(List.of(10086L, "trydofor", new BigDecimal("3.14"), LocalDateTime.of(2022, 10, 24, 12, 34, 56)));
        JooqJournalDiffConverter c = new JooqJournalDiffConverter();
        final String s = c.to(d0);
        log.warn("JournalDiff JSON ={}", s);
        // Type missing
        d0.setValue1(List.of("10086", "trydofor", "3.14", "2022-10-24 12:34:56"));
        final JournalDiff d1 = c.from(s);
        Assertions.assertEquals(d0, d1);
    }
}

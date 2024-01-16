package pro.fessional.wings.warlock.service.auth;

import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService.SimpleTerm;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService.Term;

/**
 * @author trydofor
 * @since 2022-11-08
 */
@Slf4j
class WarlockTicketServiceTest {

    @Test
    @TmsLink("C14054")
    public void testTerm() {
        Term t0 = new SimpleTerm();
        t0.setUserId(10086L);
        final String s0 = Term.encode(t0);
        log.info("s0={}", s0);
        final SimpleTerm st0 = new SimpleTerm();
        Assertions.assertTrue(st0.decode(s0));
        Assertions.assertEquals(t0, st0);

        Term t1 = new SimpleTerm();
        t1.setUserId(10086L);
        t1.setType(Term.TypeAuthorizeCode);
        final String s1 = Term.encode(t1);
        log.info("s1={}", s1);
        final SimpleTerm st1 = new SimpleTerm();
        Assertions.assertTrue(st1.decode(s1));
        Assertions.assertEquals(t1, st1);

        Term t2 = new SimpleTerm();
        t2.setUserId(10086L);
        t2.setType(Term.TypeAccessToken);
        final String s2 = Term.encode(t2);
        log.info("s2={}", s2);
        final SimpleTerm st2 = new SimpleTerm();
        Assertions.assertTrue(st2.decode(s2));
        Assertions.assertEquals(t2, st2);

        Term t3 = new SimpleTerm();
        t3.setUserId(10086L);
        t3.setType(Term.TypeAccessToken);
        t3.setScopes("a b c");
        final String s3 = Term.encode(t3);
        log.info("s3={}", s3);
        final SimpleTerm st3 = new SimpleTerm();
        Assertions.assertTrue(st3.decode(s3));
        Assertions.assertEquals(t3, st3);
    }
}

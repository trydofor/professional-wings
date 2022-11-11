package pro.fessional.wings.warlock.service.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService.Term;

/**
 * @author trydofor
 * @since 2022-11-08
 */
class WarlockTicketServiceTest {


    @Test
    public void testTerm() {
        Term t0 = new Term();
        t0.setUserId(10086L);
        final String s0 = Term.encode(t0);
        System.out.println(s0);
        Assertions.assertEquals(t0, Term.decode(s0));

        Term t1 = new Term();
        t1.setUserId(10086L);
        t1.setType(Term.TypeAuthorizeCode);
        final String s1 = Term.encode(t1);
        System.out.println(s1);
        Assertions.assertEquals(t1, Term.decode(s1));


        Term t2 = new Term();
        t2.setUserId(10086L);
        t2.setType(Term.TypeAccessToken);
        final String s2 = Term.encode(t2);
        System.out.println(s2);
        Assertions.assertEquals(t2, Term.decode(s2));

        Term t3 = new Term();
        t3.setUserId(10086L);
        t3.setType(Term.TypeAccessToken);
        t3.setScopes("a b c");
        final String s3 = Term.encode(t3);
        System.out.println(s3);
        Assertions.assertEquals(t3, Term.decode(s3));
    }
}

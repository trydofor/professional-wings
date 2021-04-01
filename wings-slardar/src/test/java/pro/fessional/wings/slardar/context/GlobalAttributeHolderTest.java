package pro.fessional.wings.slardar.context;

import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * @author trydofor
 * @since 2021-03-31
 */
class GlobalAttributeHolderTest {

    public interface Solos {
        GlobalAttributeHolder.Reg<Integer, String> PasssaltByUid = new GlobalAttributeHolder.Reg<Integer, String>() {};
        GlobalAttributeHolder.Reg<Integer, Set<String>> PermitsByUid = new GlobalAttributeHolder.Reg<Integer, Set<String>>() {};
    }

    @Test
    public void testSolo() {
        System.out.println(Solos.PasssaltByUid);
        System.out.println(Solos.PermitsByUid);
    }
}

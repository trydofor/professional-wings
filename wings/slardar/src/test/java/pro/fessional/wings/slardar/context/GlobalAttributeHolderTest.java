package pro.fessional.wings.slardar.context;

import org.junit.jupiter.api.Test;
import pro.fessional.wings.slardar.context.GlobalAttributeHolder.Reg;

import java.util.Set;

/**
 * @author trydofor
 * @since 2021-03-31
 */
class GlobalAttributeHolderTest {

    public interface Solos {
        Reg<Integer, String> PasssaltByUid = new Reg<>() {};
        Reg<Integer, Set<String>> PermitsByUid = new Reg<>() {};
    }

    @Test
    public void testSolo() {
        System.out.println(Solos.PasssaltByUid);
        System.out.println(Solos.PermitsByUid);
    }
}

package pro.fessional.wings.slardar.context;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.slardar.context.GlobalAttributeHolder.Reg;

import java.util.Set;

/**
 * @author trydofor
 * @since 2021-03-31
 */
@Slf4j
class GlobalAttributeHolderTest {

    public interface Solos {
        Reg<Integer, String> PasssaltByUid = new Reg<>() {};
        Reg<Integer, Set<String>> PermitsByUid = new Reg<>() {};
    }

    @Test
    public void testSolo() {
        log.info("PasssaltByUid={}", Solos.PasssaltByUid);
        log.info("PermitsByUid={}", Solos.PermitsByUid);
    }
}

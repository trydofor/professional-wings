package pro.fessional.wings.slardar.context;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pro.fessional.mirana.best.TypedReg;

import java.util.Set;

/**
 * @author trydofor
 * @since 2021-03-31
 */
@Slf4j
class GlobalAttributeHolderTest {

    public interface Solos {
        TypedReg<Integer, String> PasssaltByUid = new TypedReg<>() {};
        TypedReg<Integer, Set<String>> PermitsByUid = new TypedReg<>() {};
    }

    @Test
    public void testSolo() {
        log.info("PasssaltByUid={}", Solos.PasssaltByUid);
        log.info("PermitsByUid={}", Solos.PermitsByUid);
    }
}

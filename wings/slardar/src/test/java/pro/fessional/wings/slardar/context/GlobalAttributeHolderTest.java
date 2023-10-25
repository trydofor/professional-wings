package pro.fessional.wings.slardar.context;

import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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
    @TmsLink("C13004")
    public void testSolo() {
        GlobalAttributeHolder.regLoader(Solos.PasssaltByUid, integerStringKey -> "salt");
        GlobalAttributeHolder.regLoader(Solos.PermitsByUid, integerStringKey -> Set.of("prd", "dev"));
        String v1 = GlobalAttributeHolder.tryAttr(Solos.PasssaltByUid, 1);
        Set<String> v2 = GlobalAttributeHolder.tryAttr(Solos.PermitsByUid, 1);
        Assertions.assertEquals("salt", v1);
        Assertions.assertEquals(Set.of("prd", "dev"), v2);
    }
}

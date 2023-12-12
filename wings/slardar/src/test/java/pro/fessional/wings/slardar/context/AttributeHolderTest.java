package pro.fessional.wings.slardar.context;

import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.mirana.best.TypedReg;
import pro.fessional.mirana.time.Sleep;

import java.util.Set;

/**
 * @author trydofor
 * @since 2021-03-31
 */
@Slf4j
class AttributeHolderTest {

    public interface Solos {
        TypedReg<Integer, String> PasssaltByUid = new TypedReg<>() {};
        TypedReg<Integer, Set<String>> PermitsByUid = new TypedReg<>() {};
    }

    @Test
    @TmsLink("C13004")
    public void testSolo() {
        AttributeHolder.regLoader(Solos.PasssaltByUid, integerStringKey -> "salt");
        AttributeHolder.regLoader(Solos.PermitsByUid, integerStringKey -> Set.of("prd", "dev"));
        String v1 = AttributeHolder.tryAttr(Solos.PasssaltByUid, 1);
        Set<String> v2 = AttributeHolder.tryAttr(Solos.PermitsByUid, 1);
        Assertions.assertEquals("salt", v1);
        Assertions.assertEquals(Set.of("prd", "dev"), v2);

        Assertions.assertEquals(Integer.class, Solos.PasssaltByUid.keyType);
        Assertions.assertEquals(String.class, Solos.PasssaltByUid.valType);

        System.out.println(">>>" + Solos.PasssaltByUid.regType.getName().substring(Solos.PasssaltByUid.regType.getPackageName().length() + 1));
    }

    TypedReg<Integer, String> Expiry = new TypedReg<>() {};

    @Test
    @TmsLink("C13117")
    public void testExpiry() {
        // put and get
        AttributeHolder.putAttr(Expiry, 1, "1", 1);
        Assertions.assertEquals("1", AttributeHolder.getAttr(Expiry, 1));
        Sleep.ignoreInterrupt(2000); // 1500 fail
        Assertions.assertNull(AttributeHolder.getAttr(Expiry, 1));

        // loader and try
        AttributeHolder.regLoader(Expiry, integer -> "1");
        Assertions.assertEquals("1", AttributeHolder.tryAttr(Expiry, 1, 1));
        Sleep.ignoreInterrupt(2000);
        Assertions.assertNull(AttributeHolder.getAttr(Expiry, 1));
        Assertions.assertEquals("1", AttributeHolder.tryAttr(Expiry, 1));
    }
}

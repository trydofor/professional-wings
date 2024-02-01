package pro.fessional.wings.faceless.jooqgen;

import lombok.extern.slf4j.Slf4j;
import org.jooq.meta.jaxb.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author trydofor
 * @since 2024-02-01
 */
@Slf4j
class WingsCodeGeneratorTest {

    @Test
    void config() {
        Configuration c1 = WingsCodeGenerator.config();
        Configuration c2 = WingsCodeGenerator.config(WingsCodeGeneratorTest.class.getResourceAsStream("/wings-flywave/jooq-codegen-faceless.xml"));
        String s1 = c1.toString();
        String s2 = c2.toString();
        log.debug("java config={}", s1);
        log.debug("xml config={}", s2);
        Assertions.assertEquals(format(s1), format(s2));
    }

    String format(String str){
        return str.replaceAll("\\s+", "").replace("<","\n<");
    }
}
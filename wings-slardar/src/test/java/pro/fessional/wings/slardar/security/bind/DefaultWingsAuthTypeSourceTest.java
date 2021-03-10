package pro.fessional.wings.slardar.security.bind;

import org.junit.jupiter.api.Test;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthTypeSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author trydofor
 * @since 2021-02-08
 */
class DefaultWingsAuthTypeSourceTest {

    @Test
    void printAntPath() {
        print("/login".split("\\*+")); // 1
        print("/login/*".split("\\*+")); // 1
        print("/login/*.json".split("\\*+")); // 2
        print("*/login.json".split("\\*+")); // 2
        print("/**/login*.json".split("\\*+")); // 3
    }

    void print(String[] pt) {
        System.out.println(String.join("|", pt) + ", len=" + pt.length);
    }

    @Test
    void extractVar() {
        DefaultWingsAuthTypeSource d = new DefaultWingsAuthTypeSource(null, null, null, null);
        assertNull(d.extractVar("/login", 0, 0));
        assertEquals("sms", d.extractVar("/login/sms", "/login/".length(), 0));
        assertEquals("sms", d.extractVar("/login/sms.json", "/login/".length(), ".json".length()));
        assertEquals("sms", d.extractVar("sms.json", 0, ".json".length()));
    }
}

package pro.fessional.wings.slardar.security.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author trydofor
 * @since 2021-02-08
 */
class WingsBindAuthTypeSourceDefaultTest {

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
        WingsBindAuthTypeSourceDefault d = new WingsBindAuthTypeSourceDefault(null, null, null);
        assertNull(d.extractVar("/login", 0, 0));
        assertEquals("sms", d.extractVar("/login/sms", "/login/".length(), 0));
        assertEquals("sms", d.extractVar("/login/sms.json", "/login/".length(), ".json".length()));
        assertEquals("sms", d.extractVar("sms.json", 0, ".json".length()));
    }
}

package pro.fessional.wings.slardar.security.bind;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthTypeSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        DefaultWingsAuthTypeSource d1 = new DefaultWingsAuthTypeSource("/{authType}/*.json", null, null, null);
        assertEquals("github", d1.parsePathVar("/github/a.json"));
        DefaultWingsAuthTypeSource d2 = new DefaultWingsAuthTypeSource("/*/*.json", null, null, null);
        assertEquals("github", d2.parsePathVar("/github/a.json"));
        DefaultWingsAuthTypeSource d3 = new DefaultWingsAuthTypeSource("{authType}/*.json", null, null, null);
        assertEquals("github", d3.parsePathVar("github/a.json"));
        DefaultWingsAuthTypeSource d4 = new DefaultWingsAuthTypeSource("*/*.json", null, null, null);
        assertEquals("github", d4.parsePathVar("github/a.json"));
        DefaultWingsAuthTypeSource d5 = new DefaultWingsAuthTypeSource("*/a.json", null, null, null);
        assertEquals("github", d5.parsePathVar("github/a.json"));
        DefaultWingsAuthTypeSource d6 = new DefaultWingsAuthTypeSource("*/", null, null, null);
        assertEquals("github", d6.parsePathVar("github/a.json"));
        DefaultWingsAuthTypeSource d7 = new DefaultWingsAuthTypeSource("*/a.json", null, null, null);
        assertEquals("github", d7.parsePathVar("github/a.json"));
        DefaultWingsAuthTypeSource d8 = new DefaultWingsAuthTypeSource("/auth/{authType}/a.json", null, null, null);
        assertEquals("github", d8.parsePathVar("/auth/github/a.json"));
    }

    @Test
    void extractException() {
        throwsAe("**/{authType}/a.json");
        throwsAe("*/{authType}/a.json");
        throwsAe("/{authType}*/a.json");
        throwsAe("/**/*/a.json");
    }

    void throwsAe(String path) {
        final IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultWingsAuthTypeSource(path, null, null, null));
        ex.printStackTrace();
    }
}

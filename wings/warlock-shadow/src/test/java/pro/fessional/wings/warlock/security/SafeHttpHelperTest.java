package pro.fessional.wings.warlock.security;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

/**
 * @author trydofor
 * @since 2022-11-29
 */
class SafeHttpHelperTest {

    @Test
    @TmsLink("C14051")
    void isSafeRedirect() {
        Set<String> hosts = Set.of("localhost", "[107:0:0:0:200:7051]");
        Assertions.assertFalse(SafeHttpHelper.isSafeRedirect("", hosts));
        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("local", null));
        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("local", Collections.emptySet()));

        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("http://localhost", hosts));
        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("https://localhost", hosts));
        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("http://localhost:80", hosts));
        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("https://localhost:8080", hosts));
        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("http://localhost:80/a.html", hosts));
        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("https://localhost:8080/a.html", hosts));
        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("http://localhost:80?a=1", hosts));
        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("https://localhost:8080?a=1", hosts));
        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("http://localhost:80#a", hosts));
        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("https://localhost:8080#a", hosts));
        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("http://[107:0:0:0:200:7051]:80#a", hosts));
        Assertions.assertTrue(SafeHttpHelper.isSafeRedirect("https://[107:0:0:0:200:7051]:80#a", hosts));
    }

    @Test
    @TmsLink("C14052")
    void parseHostPort() {
        Assertions.assertNull(SafeHttpHelper.parseHostPort("localhost"));
        Assertions.assertEquals("localhost", SafeHttpHelper.parseHostPort("http://localhost"));
        Assertions.assertEquals("localhost", SafeHttpHelper.parseHostPort("https://localhost"));
        Assertions.assertEquals("localhost:80", SafeHttpHelper.parseHostPort("http://localhost:80"));
        Assertions.assertEquals("localhost:8080", SafeHttpHelper.parseHostPort("https://localhost:8080"));
        Assertions.assertEquals("localhost:80", SafeHttpHelper.parseHostPort("http://localhost:80/a.html"));
        Assertions.assertEquals("localhost:8080", SafeHttpHelper.parseHostPort("https://localhost:8080/a.html"));
        Assertions.assertEquals("localhost:80", SafeHttpHelper.parseHostPort("http://localhost:80?a=1"));
        Assertions.assertEquals("localhost:8080", SafeHttpHelper.parseHostPort("https://localhost:8080?a=1"));
        Assertions.assertEquals("localhost:80", SafeHttpHelper.parseHostPort("http://localhost:80#a"));
        Assertions.assertEquals("localhost:8080", SafeHttpHelper.parseHostPort("https://localhost:8080#a"));
        Assertions.assertEquals("[107:0:0:0:200:7051]:80", SafeHttpHelper.parseHostPort("http://[107:0:0:0:200:7051]:80#a"));
        Assertions.assertEquals("[107:0:0:0:200:7051]:80", SafeHttpHelper.parseHostPort("https://[107:0:0:0:200:7051]:80#a"));
    }
}

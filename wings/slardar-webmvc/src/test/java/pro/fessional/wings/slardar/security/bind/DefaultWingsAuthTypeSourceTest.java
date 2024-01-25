package pro.fessional.wings.slardar.security.bind;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import pro.fessional.wings.slardar.security.WingsAuthHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author trydofor
 * @since 2021-02-08
 */
class DefaultWingsAuthTypeSourceTest {

    private final PathPatternParser patternParser = PathPatternParser.defaultInstance;

    @Test
    @TmsLink("C13080")
    void parsePathPattern() {
        final PathPattern ptn = patternParser.parse("/auth/{authType}/login.json");
        final PathPattern.PathMatchInfo f1 = ptn.matchAndExtract(PathContainer.parsePath("/auth/username/login.json"));
        assertNotNull(f1);
        assertEquals("username", f1.getUriVariables().get(WingsAuthHelper.AuthType));

        final PathPattern.PathMatchInfo f2 = ptn.matchAndExtract(PathContainer.parsePath("/auth/user/name/login.json"));
        assertNull(f2);
    }

    @Test
    @TmsLink("C13081")
    void parsePathPatternRegexp() {
        final PathPattern ptn = patternParser.parse("/auth/{authType:[^-]+}{splitter:-?}{authZone:[^-]*}/login.json");
        final PathPattern.PathMatchInfo f1 = ptn.matchAndExtract(PathContainer.parsePath("/auth/username-admin/login.json"));
        assertNotNull(f1);
        assertEquals("username", f1.getUriVariables().get(WingsAuthHelper.AuthType));
        assertEquals("admin", f1.getUriVariables().get(WingsAuthHelper.AuthZone));

        final PathPattern.PathMatchInfo f2 = ptn.matchAndExtract(PathContainer.parsePath("/auth/username-/login.json"));
        assertNotNull(f2);
        assertEquals("username", f2.getUriVariables().get(WingsAuthHelper.AuthType));
        assertEquals("", f2.getUriVariables().get(WingsAuthHelper.AuthZone));

        final PathPattern.PathMatchInfo f3 = ptn.matchAndExtract(PathContainer.parsePath("/auth/username/login.json"));
        assertNotNull(f3);
        assertEquals("username", f3.getUriVariables().get(WingsAuthHelper.AuthType));
        assertEquals("", f3.getUriVariables().get(WingsAuthHelper.AuthZone));
    }
}

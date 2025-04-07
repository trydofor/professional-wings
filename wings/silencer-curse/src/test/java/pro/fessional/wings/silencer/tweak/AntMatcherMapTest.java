package pro.fessional.wings.silencer.tweak;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@TmsLink("C11038")
class AntMatcherMapTest {

    @Test
    void testPutAndGet() {
        AntMatcherMap<String> map = new AntMatcherMap<>();

        // Test basic put and get
        assertNull(map.put("key1", "value1"));
        assertEquals("value1", map.get("key1"));

        // Test overwrite
        assertEquals("value1", map.put("key1", "value2"));
        assertEquals("value2", map.get("key1"));

        // Test ant pattern matching
        assertNull(map.put("/api/**", "apiValue"));
        assertEquals("apiValue", map.get("/api/test"));
        assertEquals("apiValue", map.get("/api/another/test"));

        // Test exact match takes precedence over pattern
        assertNull(map.put("/api/specific", "specificValue"));
        assertEquals("specificValue", map.get("/api/specific"));
    }

    @Test
    void testPutAll() {
        AntMatcherMap<String> map = new AntMatcherMap<>();
        Map<String, String> input = new HashMap<>();
        input.put("key1", "value1");
        input.put("/api/**", "apiValue");

        map.putAll(input);

        assertEquals("value1", map.get("key1"));
        assertEquals("apiValue", map.get("/api/test"));
    }

    @Test
    void testCustomAntMatcher() {
        AntPathMatcher customMatcher = new AntPathMatcher();
        AntMatcherMap<String> map = new AntMatcherMap<>(customMatcher);

        // Verify the custom matcher is used
        assertSame(customMatcher, map.antMatcher);

        // Test pattern matching works with custom matcher
        map.put("/api/**", "apiValue");
        assertEquals("apiValue", map.get("/api/test"));
    }

    @Test
    void testEdgeCases() {
        AntMatcherMap<String> map = new AntMatcherMap<>();

        // Test empty string key
        assertNull(map.put("", "emptyValue"));
        assertEquals("emptyValue", map.get(""));

        // Test pattern with no matches
        assertNull(map.put("/no/match/**", "noMatch"));
        assertNull(map.get("/different/pattern"));

        // Test multiple patterns, most specific should match
        map.put("/a/*/c", "star");
        map.put("/a/**/c", "doubleStar");
        assertEquals("star", map.get("/a/b/c")); // More specific pattern should win
    }
}

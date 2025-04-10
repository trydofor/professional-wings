package pro.fessional.wings.silencer.support;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2024-06-28
 */
@SpringBootTest
class PropHelperTest {

    @Test
    @TmsLink("C11036")
    void testCommaList() {
        assertTrue(PropHelper.commaList("").isEmpty());
        assertTrue(PropHelper.commaList(",").isEmpty());
        assertTrue(PropHelper.commaList(",,").isEmpty());

        Assertions.assertEquals(List.of("1", "2"), PropHelper.commaList("1,,2"));
        Assertions.assertEquals(List.of("1", "2"), PropHelper.commaList("1 , - , , 2 "));
        Assertions.assertEquals(List.of(1, 2), PropHelper.commaList("1,,2", Integer::valueOf));
        Assertions.assertEquals(List.of(1L, 2L), PropHelper.commaList("1,,2", Long::valueOf));
        Assertions.assertEquals(List.of(new BigDecimal("1"), new BigDecimal("2")), PropHelper.commaList("1,,2", BigDecimal::new));
        Assertions.assertEquals(List.of(1, 2), PropHelper.commaList("1 , - , , 2 ", Integer::valueOf));
        Assertions.assertEquals(List.of(1L, 2L), PropHelper.commaList("1 , - , , 2 ", Long::valueOf));
        Assertions.assertEquals(List.of(new BigDecimal("1"), new BigDecimal("2")), PropHelper.commaList("1 , - , , 2 ", BigDecimal::new));

        Assertions.assertEquals(List.of(""), PropHelper.commaList("", false, false));
        Assertions.assertEquals(List.of("", ""), PropHelper.commaList(",", false, false));
        Assertions.assertEquals(List.of("", "", ""), PropHelper.commaList(",,", false, false));
        Assertions.assertEquals(List.of("1", "", "2"), PropHelper.commaList("1,,2", false, false));
        Assertions.assertEquals(List.of("1 ", " ", " 2 "), PropHelper.commaList("1 , , 2 ", false, false));

        Assertions.assertNull(PropHelper.commaString((String[]) null));
        Assertions.assertEquals("", PropHelper.commaString(List.of("")));
        Assertions.assertEquals(",", PropHelper.commaString(List.of("","")));
        Assertions.assertEquals(",,1", PropHelper.commaString(List.of("", "", "1")));
        Assertions.assertEquals(",,1 ", PropHelper.commaString(List.of("", "", "1 ")));
        Assertions.assertEquals(",,1 ,-", PropHelper.commaString(List.of("", "", "1 ", "-")));

        Assertions.assertEquals("", PropHelper.commaString(List.of(""), true, true));
        Assertions.assertEquals("", PropHelper.commaString(List.of("", ""), true, true));
        Assertions.assertEquals("1", PropHelper.commaString(List.of("", "", "1"), true, true));
        Assertions.assertEquals("1", PropHelper.commaString(List.of("", "", "1 "), true, true));
        Assertions.assertEquals("1", PropHelper.commaString(List.of("", "", "1 ", "- "), true, true));
        Assertions.assertEquals("1 ,- ", PropHelper.commaString(List.of("", "", "1 ", "- "), false, true));
        Assertions.assertEquals("1 ,- ", PropHelper.commaString(List.of("", "", "1 ", "- "), false, true, Object::toString));
    }

    @Test
    @TmsLink("C11037")
    void testResourceString() {
        ClassPathResource app = new ClassPathResource("application.properties");
        assertTrue(app.exists());
        String res1 = PropHelper.stringResource(app);
        Assertions.assertEquals("classpath:application.properties", res1);

        Resource res2 = PropHelper.resourceString("classpath:application.properties");
        assertTrue(res2.exists());

        Resource res3 = PropHelper.resourceString("optional:classpath:application.properties");
        assertTrue(res3.exists());

        Resource res4 = PropHelper.resourceString("optional:classpath:application.properties-404");
        assertFalse(res4.exists());

        Resource res5 = PropHelper.resourceString("optional:file:./application.properties-404");
        assertFalse(res5.exists());
    }
    @Test
    public void testInvalid() {
        assertTrue(PropHelper.invalid(null));
        assertTrue(PropHelper.invalid(""));
        assertTrue(PropHelper.invalid("   "));
        assertTrue(PropHelper.invalid(PropHelper.DisabledValue));
        assertTrue(PropHelper.invalid(PropHelper.MaskingValue));
        assertFalse(PropHelper.invalid("valid"));
    }

    @Test
    public void testValid() {
        assertFalse(PropHelper.valid(null));
        assertFalse(PropHelper.valid(""));
        assertFalse(PropHelper.valid("   "));
        assertFalse(PropHelper.valid(PropHelper.DisabledValue));
        assertFalse(PropHelper.valid(PropHelper.MaskingValue));
        assertTrue(PropHelper.valid("valid"));
    }

    @Test
    public void testOnlyValidCollection() {
        // Test null input
        assertEquals(0, PropHelper.onlyValid((Collection<String>) null).size());

        // Test with mixed valid and invalid values
        List<String> input = Arrays.asList(null, "", "   ", PropHelper.DisabledValue, PropHelper.MaskingValue, "valid1", "valid2");
        LinkedHashSet<String> result = PropHelper.onlyValid(input);
        assertEquals(2, result.size());
        assertTrue(result.contains("valid1"));
        assertTrue(result.contains("valid2"));

        // Test empty collection
        assertEquals(0, PropHelper.onlyValid(Collections.emptyList()).size());
    }

    @Test
    public void testOnlyValidMap() {
        // Test null input
        assertEquals(0, PropHelper.onlyValid((Map<String, String>) null).size());

        // Test with mixed valid and invalid values
        Map<String, String> input = new HashMap<>();
        input.put("k1", null);
        input.put("k2", "");
        input.put("k3", "   ");
        input.put("k4", PropHelper.DisabledValue);
        input.put("k5", PropHelper.MaskingValue);
        input.put("k6", "valid1");
        input.put("k7", "valid2");

        LinkedHashMap<String, String> result = PropHelper.onlyValid(input);
        assertEquals(2, result.size());
        assertEquals("valid1", result.get("k6"));
        assertEquals("valid2", result.get("k7"));

        // Test empty map
        assertEquals(0, PropHelper.onlyValid(Collections.emptyMap()).size());
    }

    @Test
    public void testMergeToInvalid() {
        // Test null that map
        Map<String, String> thiz = new HashMap<>();
        thiz.put("k1", "valid");
        thiz.put("k2", PropHelper.DisabledValue);
        PropHelper.mergeToInvalid(thiz, null);
        assertEquals(2, thiz.size());
        assertEquals("valid", thiz.get("k1"));
        assertEquals(PropHelper.DisabledValue, thiz.get("k2"));

        // Test empty that map
        Map<String, String> that = new HashMap<>();
        PropHelper.mergeToInvalid(thiz, that);
        assertEquals(2, thiz.size());

        // Test merge scenario
        that.put("k2", "replaced");
        that.put("k3", "new");
        PropHelper.mergeToInvalid(thiz, that);
        assertEquals(2, thiz.size());
        assertEquals("valid", thiz.get("k1"));
        assertEquals("replaced", thiz.get("k2"));

        // Test empty thiz map
        Map<String, String> emptyThiz = new HashMap<>();
        PropHelper.mergeToInvalid(emptyThiz, that);
        assertEquals(2, emptyThiz.size());
    }

    @Test
    public void testPrefixOptional() {
        assertEquals("optional:test", PropHelper.prefixOptional("test"));
        assertEquals("optional:test", PropHelper.prefixOptional("optional:test"));
    }

    @Test
    public void testRemoveOptional() {
        // Test with default value
        assertEquals("test", PropHelper.removeOptional("optional:test", "default"));
        assertEquals("default", PropHelper.removeOptional("test", "default"));

        // Test multiple optional prefixes
        assertEquals("test", PropHelper.removeOptional("optional:optional:test", "default"));

        // Test without default value
        assertEquals("test", PropHelper.removeOptional("optional:test"));
        assertEquals("plain", PropHelper.removeOptional("plain"));
    }

    @Test
    public void testCommaString() {
        // Test null input
        assertNull(PropHelper.commaString((Object[]) null));
        assertNull(PropHelper.commaString((Collection<?>) null));

        // Test basic array
        String[] array = {"a", "b", "c"};
        assertEquals("a,b,c", PropHelper.commaString(array));
        assertEquals("a,b,c", PropHelper.commaString(array, Object::toString));

        // Test basic collection
        List<String> list = Arrays.asList("a", "b", "c");
        assertEquals("a,b,c", PropHelper.commaString(list));
        assertEquals("a,b,c", PropHelper.commaString(list, Object::toString));

        // Test with converter
        Function<Object, String> converter = obj -> obj.toString().toUpperCase();
        assertEquals("A,B,C", PropHelper.commaString(array, converter));
        assertEquals("A,B,C", PropHelper.commaString(list, converter));

        // Test with strip and drop
        String[] mixedArray = {" a ", "  ", "b", null, "c"};
        assertEquals("a,b,c", PropHelper.commaString(mixedArray, true, true));
        assertEquals(" a ,  ,b,,c", PropHelper.commaString(mixedArray, false, false));
        assertEquals("a,b,c", PropHelper.commaString(mixedArray, true, true, Object::toString));
        assertEquals(" a ,  ,b,,c", PropHelper.commaString(mixedArray, false, false, Object::toString));
    }

    @Test
    public void testDelimitedString() {
        // Test null input
        assertNull(PropHelper.delimitedString((Object[]) null, "|", false, false));
        assertNull(PropHelper.delimitedString((Collection<?>) null, "|", false, false));

        // Test custom delimiter
        String[] array = {"a", "b", "c"};
        assertEquals("a|b|c", PropHelper.delimitedString(array, "|", false, false));

        // Test with converter
        Function<Object, String> converter = obj -> obj.toString().toUpperCase();
        assertEquals("A|B|C", PropHelper.delimitedString(array, "|", false, false, converter));

        // Test with strip and drop
        String[] mixedArray = {" a ", "  ", "b", null, "c"};
        assertEquals("a|b|c", PropHelper.delimitedString(mixedArray, "|", true, true));
        assertEquals(" a |  |b||c", PropHelper.delimitedString(mixedArray, "|", false, false));
    }

    @Test
    public void testCommaArray() {
        // Test null or blank input
        assertEquals(0, PropHelper.commaArray(null).length);
        assertEquals(0, PropHelper.commaArray("").length);

        // Test basic case
        String[] result = PropHelper.commaArray("a,b,c");
        assertEquals(3, result.length);
        assertArrayEquals(new String[]{"a", "b", "c"}, result);

        // Test with strip and drop
        result = PropHelper.commaArray(" a ,  ,b,,c", false, false);
        assertEquals(5, result.length);
        assertArrayEquals(new String[]{" a ", "  ", "b", "", "c"}, result);

        result = PropHelper.commaArray(" a ,  ,b,,c", true, true);
        assertEquals(3, result.length);
        assertArrayEquals(new String[]{"a", "b", "c"}, result);
    }

    @Test
    public void testCommaList2() {
        // Test null or blank input
        assertEquals(0, PropHelper.commaList(null).size());
        assertEquals(0, PropHelper.commaList("").size());

        // Test basic case
        List<String> result = PropHelper.commaList("a,b,c");
        assertEquals(3, result.size());
        assertEquals(Arrays.asList("a", "b", "c"), result);

        // Test with strip and drop
        result = PropHelper.commaList(" a ,  ,b,,c", false, false);
        assertEquals(5, result.size());
        assertEquals(Arrays.asList(" a ", "  ", "b", "", "c"), result);

        result = PropHelper.commaList(" a ,  ,b,,c", true, true);
        assertEquals(3, result.size());
        assertEquals(Arrays.asList("a", "b", "c"), result);

        // Test with converter
        Function<String, Integer> converter = Integer::parseInt;
        List<Integer> intResult = PropHelper.commaList("1,2,3", converter);
        assertEquals(3, intResult.size());
        assertEquals(Arrays.asList(1, 2, 3), intResult);
    }

    @Test
    public void testDelimitedList() {
        // Test null or blank input
        assertEquals(0, PropHelper.delimitedList(null, "|", false, false).size());
        assertEquals(0, PropHelper.delimitedList("", "|", true, true).size());

        // Test custom delimiter
        List<String> result = PropHelper.delimitedList("a|b|c", "|", false, false);
        assertEquals(3, result.size());
        assertEquals(Arrays.asList("a", "b", "c"), result);

        // Test with strip and drop
        result = PropHelper.delimitedList(" a |  |b||c", "|", true, true);
        assertEquals(3, result.size());
        assertEquals(Arrays.asList("a", "b", "c"), result);

        // Test with converter
        Function<String, Integer> converter = Integer::parseInt;
        List<Integer> intResult = PropHelper.delimitedList("1|2|3", "|", false, false, converter);
        assertEquals(3, intResult.size());
        assertEquals(Arrays.asList(1, 2, 3), intResult);
    }
}
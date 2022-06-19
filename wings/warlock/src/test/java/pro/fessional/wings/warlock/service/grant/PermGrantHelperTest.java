package pro.fessional.wings.warlock.service.grant;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2021-03-07
 */
class PermGrantHelperTest {

    @Test
    void canInherit() {
        assertTrue(PermGrantHelper.canInherit("*", "read"));
        assertTrue(PermGrantHelper.canInherit(".*", "system.read"));
        assertTrue(PermGrantHelper.canInherit("system.*", "system.read"));
        assertTrue(PermGrantHelper.canInherit("system.read", "system.menu.read"));
        assertTrue(PermGrantHelper.canInherit("system.menu.*", "system.menu.write"));
        assertFalse(PermGrantHelper.canInherit("system.read", "system.menu.write"));
    }

    @Test
    void grantRole() {
        Map<Long, String> roleMap = new HashMap<>();
        roleMap.put(1L, "ROLE_1");
        roleMap.put(11L, "ROLE_1.1");
        roleMap.put(111L, "ROLE_1.1.1");
        roleMap.put(112L, "ROLE_1.1.2");
        roleMap.put(221L, "ROLE_2.2.1");

        Map<Long, Set<Long>> roleRef = new HashMap<>();

        roleRef.put(1L, new HashSet<>(Collections.singletonList(11L)));
        roleRef.put(11L, new HashSet<>(Arrays.asList(111L, 112L)));

        final Set<String> set1 = PermGrantHelper.grantRole(1, roleMap, roleRef);
        assertEquals(Sets.newHashSet("ROLE_1",
                "ROLE_1.1",
                "ROLE_1.1.1",
                "ROLE_1.1.2"
        ), set1);

        final Set<String> set2 = PermGrantHelper.grantRole(11, roleMap, roleRef);
        assertEquals(Sets.newHashSet(
                "ROLE_1.1",
                "ROLE_1.1.1",
                "ROLE_1.1.2"
        ), set2);
    }
}

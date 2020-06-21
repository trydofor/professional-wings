package pro.fessional.wings.example.service.authrole;

import pro.fessional.mirana.math.AnyIntegerUtil;
import pro.fessional.wings.example.enums.auto.Authority;
import pro.fessional.wings.faceless.enums.ConstantEnumUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2020-06-21
 */
public class AuthEnumUtil {

    /**
     * 变成 id和code的map，支持负数标记删除
     *
     * @param map map
     * @param ids auth id
     */
    public static void fillAuth(Map<Integer, String> map, String... ids) {
        if (ids == null || ids.length == 0) return;
        for (String id : ids) {
            int i = AnyIntegerUtil.val32(id);
            Authority a;
            if (i < 0) {
                a = ConstantEnumUtil.idOrNull(-i, Authority.values());
            } else {
                a = ConstantEnumUtil.idOrNull(i, Authority.values());
            }
            if (a != null) {
                map.put(i, a.getCode());
            }
        }
    }

    /**
     * 按负数删除，整理 map
     *
     * @param map map
     */
    public static void trimAuth(Map<Integer, String> map) {
        if (map == null || map.isEmpty()) return;

        Set<Integer> remove = new HashSet<>();
        for (Integer i : map.keySet()) {
            if(i < 0) remove.add(-i);
        }

        map.entrySet().removeIf(e -> e.getKey() < 0 || remove.contains(e.getKey()));
    }
}

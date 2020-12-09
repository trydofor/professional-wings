package pro.fessional.wings.faceless.service.lightid.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.service.lightid.LightIdAware;
import pro.fessional.wings.faceless.service.lightid.LightIdService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 对class进行了缓存
 *
 * @author trydofor
 * @since 2020-12-03
 */
public abstract class AbstractLightIdService implements LightIdService {

    protected final ConcurrentHashMap<Class<?>, String> classCache = new ConcurrentHashMap<>();

    @Override
    public long getId(@NotNull Class<? extends LightIdAware> table, int block) {
        String key = classCache.computeIfAbsent(table, k -> {
            String name = table.getSimpleName();
            int len = name.endsWith("Table") ? name.length() - 5 : name.length();
            StringBuilder sb = new StringBuilder(len + 10);
            for (int i = 0; i < len; i++) {
                char c = name.charAt(i);
                if (c >= 'A' && c <= 'Z') {
                    if (i > 0) sb.append('_');
                    sb.append((char) (c + 32));
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        });

        return getId(key, block);
    }
}

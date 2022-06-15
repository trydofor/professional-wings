package pro.fessional.wings.slardar.cache.spring;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 可evict单key，多key，全部，或跳过。写入时非线程安全
 *
 * @author trydofor
 * @see SimpleKeyGenerator#generateKey(Object...)
 * @since 2022-04-18
 */
public class CacheEvictResult {

    public static final CacheEvictResult EvictAll = new CacheEvictResult(true);
    public static volatile boolean wingsSupport = false;

    @Getter @Setter
    private boolean evictAll = false;
    @Getter @Setter
    private List<Object> evictKey = Collections.emptyList();

    public CacheEvictResult() {
    }

    public CacheEvictResult(boolean all) {
        this.evictAll = all;
    }

    public CacheEvictResult(boolean all, @NotNull List<Object> keys) {
        this.evictAll = all;
        this.evictKey = keys;
    }

    /**
     * 兼容非wings版，获得key
     */
    public Object getKey() {
        if (wingsSupport) {
            return this;
        }
        else {
            return evictKey.isEmpty() ? null : evictKey.iterator().next();
        }
    }

    public CacheEvictResult addKey(Object... arg) {
        if (evictKey.isEmpty()) {
            evictKey = new LinkedList<>();
        }

        evictKey.add(SimpleKeyGenerator.generateKey(arg));
        return this;
    }
}

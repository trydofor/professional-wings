package pro.fessional.wings.slardar.cache.spring;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Can evict single key, multiple keys, all keys, or skip.
 * Non-thread-safe on write
 *
 * @author trydofor
 * @see SimpleKeyGenerator#generateKey(Object...)
 * @since 2022-04-18
 */
public class CacheEvictMultiKeys {

    public static final CacheEvictMultiKeys EvictAll = new CacheEvictMultiKeys(true);
    public static volatile boolean wingsSupport = false;

    @Getter @Setter
    private boolean evictAll = false;
    @Getter @Setter
    private List<Object> evictKey = Collections.emptyList();

    public CacheEvictMultiKeys() {
    }

    public CacheEvictMultiKeys(boolean all) {
        this.evictAll = all;
    }

    public CacheEvictMultiKeys(boolean all, @NotNull List<Object> keys) {
        this.evictAll = all;
        this.evictKey = keys;
    }

    /**
     * get key, compatible with non-wings style
     */
    public Object getKey() {
        if (wingsSupport) {
            return this;
        }
        else {
            return evictKey.isEmpty() ? null : evictKey.iterator().next();
        }
    }

    @Contract("_->this")
    public CacheEvictMultiKeys addKey(Object... arg) {
        if (evictKey.isEmpty()) {
            evictKey = new LinkedList<>();
        }

        evictKey.add(SimpleKeyGenerator.generateKey(arg));
        return this;
    }
}

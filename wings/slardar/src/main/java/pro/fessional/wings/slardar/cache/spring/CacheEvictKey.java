package pro.fessional.wings.slardar.cache.spring;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Can evict multiple keys, evict all (if empty)
 * Non-thread-safe on write
 *
 * @author trydofor
 * @since 2022-04-18
 */
public class CacheEvictKey {

    public static volatile KeyGenerator KeyGenerator = new SimpleKeyGenerator();

    @Getter @Setter
    private List<Object> keys = Collections.emptyList();

    public CacheEvictKey() {
    }

    /**
     * get key, compatible with non-wings style
     */
    public Object getKey() {
        return keys.isEmpty() ? null : keys.iterator().next();
    }

    /**
     * add key by #KeyGenerator
     *
     * @see #KeyGenerator
     */
    @Contract("_->this")
    public final CacheEvictKey add(Object target, Method method, Object... arg) {
        if (keys.isEmpty()) {
            keys = new LinkedList<>();
        }

        Object gk = KeyGenerator.generate(target, method, arg);
        keys.add(gk);
        return this;
    }

    /**
     * add key by SimpleKeyGenerator
     *
     * @see SimpleKeyGenerator#generateKey(Object...)
     */
    @Contract("_->this")
    public CacheEvictKey add(Object... arg) {
        if (keys.isEmpty()) {
            keys = new LinkedList<>();
        }

        Object gk = SimpleKeyGenerator.generateKey(arg);
        keys.add(gk);
        return this;
    }
}

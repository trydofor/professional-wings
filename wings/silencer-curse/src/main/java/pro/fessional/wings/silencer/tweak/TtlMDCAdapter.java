package pro.fessional.wings.silencer.tweak;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.jetbrains.annotations.NotNull;
import org.slf4j.spi.MDCAdapter;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @see ch.qos.logback.classic.util.LogbackMDCAdapter
 * @since 2022-10-29
 */
public class TtlMDCAdapter implements MDCAdapter {

    private final ThreadLocal<Map<String, String>> valueMap = TransmittableThreadLocal.withInitial(HashMap::new);
    private final ThreadLocal<Map<String, String>> cacheMap = TransmittableThreadLocal.withInitial(Collections::emptyMap);
    private final ThreadLocal<Map<String, Deque<String>>> dequeMap = TransmittableThreadLocal.withInitial(HashMap::new);

    @Override
    public void put(String key, String val) {
        if (key == null) return;
        valueMap.get().put(key, val);
        clearCache();
    }

    @Override
    public String get(String key) {
        return key == null ? null : valueMap.get().get(key);
    }

    @Override
    public void remove(String key) {
        if (key == null) return;
        valueMap.get().remove(key);
        clearCache();
    }

    @Override
    public void clear() {
        valueMap.remove();
        cacheMap.remove();
    }

    private void clearCache() {
        cacheMap.set(Collections.emptyMap());
    }

    @NotNull
    public Map<String, String> getReadOnlyMap() {
        var map = cacheMap.get();
        if (map == Collections.EMPTY_MAP) {
            map = Map.copyOf(valueMap.get());
            cacheMap.set(map);
        }
        return map;
    }

    public Map<String, String> getCopyOfContextMap() {
        return new HashMap<>(getReadOnlyMap());
    }

    public Set<String> getKeys() {
        return getReadOnlyMap().keySet();
    }

    public void setContextMap(Map<String, String> ctx) {
        Map<String, String> map = ctx == null ? new HashMap<>() : new HashMap<>(ctx);
        valueMap.set(map);
        clearCache();
    }


    @Override
    public void pushByKey(String key, String value) {
        if (key != null) {
            dequeMap.get()
                    .computeIfAbsent(key, k -> new ArrayDeque<>())
                    .push(value);
        }
    }

    @Override
    public String popByKey(String key) {
        if (key == null) return null;

        var deque = dequeMap.get().get(key);
        return deque == null ? null : deque.pop();
    }

    @Override
    public Deque<String> getCopyOfDequeByKey(String key) {
        if (key == null) return null;

        var deque = dequeMap.get().get(key);
        return deque == null ? null : new ArrayDeque<>(deque);
    }

    @Override
    public void clearDequeByKey(String key) {
        if (key == null) return;

        var deque = dequeMap.get().get(key);
        if (deque != null) deque.clear();
    }
}

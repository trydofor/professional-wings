package pro.fessional.wings.slardar.cache;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperation;
import pro.fessional.mirana.best.StateAssert;
import pro.fessional.mirana.data.Null;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.emptyMap;

/**
 * @author trydofor
 * @since 2021-03-08
 */
@Slf4j
public class WingsCacheHelper {

    private static final Map<String, CacheManager> managers = new ConcurrentHashMap<>();
    private static CacheManager memory;
    private static CacheManager server;

    @Nullable
    public static CacheManager getCacheManager(String name) {
        if (WingsCache.Manager.Memory.equalsIgnoreCase(name)) {
            return memory;
        }
        if (WingsCache.Manager.Server.equalsIgnoreCase(name)) {
            return server;
        }
        return managers.get(name);
    }

    @Nullable
    public static Cache getCache(String manager, String cache) {
        final CacheManager cm = getCacheManager(manager);
        return cm == null ? null : cm.getCache(cache);
    }

    @NotNull
    public static Cache getMemoryCache(String name) {
        final Cache cache = memory.getCache(name);
        StateAssert.notNull(cache, "memory cache is null, name={}", name);
        return cache;
    }

    @NotNull
    public static Cache getServerCache(String name) {
        final Cache cache = server.getCache(name);
        StateAssert.notNull(cache, "server cache is null, name={}", name);
        return cache;
    }

    public static void setManagers(Map<String, CacheManager> mgs) {
        managers.putAll(mgs);
        memory = managers.get(WingsCache.Manager.Memory);
        server = managers.get(WingsCache.Manager.Server);
    }

    @NotNull
    public static CacheManager getMemory() {
        StateAssert.notNull(memory, "Memory CacheManager is null");
        return memory;
    }

    @NotNull
    public static CacheManager getServer() {
        StateAssert.notNull(server, "Server CacheManager is null");
        return server;
    }

    ///
    private static final Map<Class<?>, Map<String, Meta>> classes = new ConcurrentHashMap<>();

    public static class Meta {
        private final Map<String, Set<String>> metaMap = new HashMap<>();
        private final Map<String, CacheManager> objManager = new HashMap<>();
        private final Map<String, Set<Cache>> objCache = new HashMap<>();

        public void initOperation(String cm, Set<String> cs) {
            final Set<String> set = metaMap.computeIfAbsent(cm, k -> new HashSet<>());
            set.addAll(cs);
        }

        public Map<String, CacheManager> getManagers() {
            if (metaMap.isEmpty()) return objManager;
            if (objManager.isEmpty()) {
                for (String k : metaMap.keySet()) {
                    final CacheManager m = managers.get(k);
                    StateAssert.notNull(m, "no CacheManager for {}", k);
                    objManager.put(k, m);
                }
            }
            return objManager;
        }

        public Map<String, Set<Cache>> getCaches() {
            if (metaMap.isEmpty()) return objCache;
            if (objCache.isEmpty()) {
                for (Map.Entry<String, Set<String>> en : metaMap.entrySet()) {
                    String k = en.getKey();
                    final CacheManager m = managers.get(k);
                    StateAssert.notNull(m, "no CacheManager for {}", k);
                    Set<Cache> st = new HashSet<>();
                    for (String c : en.getValue()) {
                        st.add(m.getCache(c));
                    }
                    objCache.put(k, st);
                }
            }
            return objCache;
        }
    }

    @NotNull
    public static Map<String, Set<String>> getCacheMeta(Class<?> clz) {
        return getCacheMeta(clz, Null.Str);
    }

    @NotNull
    public static Map<String, Set<String>> getCacheMeta(Class<?> claz, String method) {
        final Map<String, Meta> map = classes.get(claz);
        if (map == null) return emptyMap();
        if (method == null) method = Null.Str;
        final Meta mt = map.get(method);
        return mt == null ? emptyMap() : mt.metaMap;
    }

    @NotNull
    public static Map<String, CacheManager> getManager(Class<?> clz) {
        return getManager(clz, Null.Str);
    }

    @NotNull
    public static Map<String, CacheManager> getManager(Class<?> claz, String method) {
        final Map<String, Meta> map = classes.get(claz);
        if (map == null) return emptyMap();
        if (method == null) method = Null.Str;
        final Meta mt = map.get(method);
        return mt == null ? emptyMap() : mt.getManagers();
    }

    @NotNull
    public static Map<String, Set<Cache>> getCaches(Class<?> clz) {
        return getCaches(clz, Null.Str);
    }

    @NotNull
    public static Map<String, Set<Cache>> getCaches(Class<?> claz, String method) {
        final Map<String, Meta> map = classes.get(claz);
        if (map == null) return emptyMap();
        if (method == null) method = Null.Str;
        final Meta mt = map.get(method);
        return mt == null ? emptyMap() : mt.getCaches();
    }

    public static void setOperation(Method method, Collection<CacheOperation> opr) {
        if (opr == null || opr.isEmpty()) return;
        final Map<String, Meta> entry = classes.computeIfAbsent(method.getDeclaringClass(), k -> new ConcurrentHashMap<>());
        final Meta top = entry.computeIfAbsent(Null.Str, k -> new Meta());
        final Meta mod = entry.computeIfAbsent(method.getName(), k -> new Meta());

        for (CacheOperation op : opr) {
            final String cm = op.getCacheManager();
            final Set<String> cs = op.getCacheNames();
            top.initOperation(cm, cs);
            mod.initOperation(cm, cs);
        }
    }
}

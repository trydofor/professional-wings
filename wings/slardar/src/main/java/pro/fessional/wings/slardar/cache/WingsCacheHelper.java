package pro.fessional.wings.slardar.cache;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperation;
import pro.fessional.mirana.best.AssertState;
import pro.fessional.mirana.data.Null;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

/**
 * @author trydofor
 * @since 2021-03-08
 */
@Slf4j
public class WingsCacheHelper {

    private static final Map<String, CacheManager> managers = new ConcurrentHashMap<>();
    private static final Map<CacheManager, Set<String>> namings = new ConcurrentHashMap<>();
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

    public static Set<String> getManagerNames(CacheManager manage) {
        if (manage == null) return emptySet();
        return namings.getOrDefault(manage, emptySet());
    }

    @Nullable
    public static Cache getCache(String manager, String cache) {
        final CacheManager cm = getCacheManager(manager);
        return cm == null ? null : cm.getCache(cache);
    }

    @NotNull
    public static Cache getMemoryCache(String name) {
        final Cache cache = memory.getCache(name);
        AssertState.notNull(cache, "memory cache is null, name={}", name);
        return cache;
    }

    @NotNull
    public static Cache getServerCache(String name) {
        final Cache cache = server.getCache(name);
        AssertState.notNull(cache, "server cache is null, name={}", name);
        return cache;
    }

    /**
     * Set CacheManager name and its Resolver
     */
    public static void putManagers(Map<String, CacheManager> mngs) {
        managers.putAll(mngs);

        memory = managers.get(WingsCache.Manager.Memory);
        server = managers.get(WingsCache.Manager.Server);

        namings.clear();
        for (Map.Entry<String, CacheManager> en : managers.entrySet()) {
            namings.computeIfAbsent(en.getValue(), k -> new HashSet<>()).add(en.getKey());
        }
    }

    @NotNull
    public static CacheManager getMemory() {
        AssertState.notNull(memory, "Memory CacheManager is null");
        return memory;
    }

    @NotNull
    public static CacheManager getServer() {
        AssertState.notNull(server, "Server CacheManager is null");
        return server;
    }

    ///
    private static final Map<Class<?>, Map<String, Meta>> classes = new ConcurrentHashMap<>();

    public static class Meta {
        private final Map<String, Set<String>> metaMap = new HashMap<>();
        private final Map<CacheManager, Set<String>> objManager = new HashMap<>();
        private final Map<String, Set<Cache>> objCache = new HashMap<>();

        public void initOperation(String cr, String cm, Set<String> cs) {
            if (!cr.isEmpty()) {
                metaMap.computeIfAbsent(cr, k -> new HashSet<>()).addAll(cs);
            }
            if (!cm.isEmpty()) {
                metaMap.computeIfAbsent(cm, k -> new HashSet<>()).addAll(cs);
            }
        }

        public Map<CacheManager, Set<String>> getManagers() {
            if (metaMap.isEmpty()) return objManager;
            if (objManager.isEmpty()) {
                for (String nm : metaMap.keySet()) {
                    final CacheManager m = getCacheManager(nm);
                    AssertState.notNull(m, "no CacheManager for {}", nm);
                    objManager.put(m, getManagerNames(m));
                }
            }
            return objManager;
        }

        public Map<String, Set<Cache>> getCaches() {
            if (metaMap.isEmpty()) return objCache;
            if (objCache.isEmpty()) {
                for (Map.Entry<String, Set<String>> en : metaMap.entrySet()) {
                    String k = en.getKey();
                    final CacheManager m = getCacheManager(k);
                    AssertState.notNull(m, "no CacheManager for {}", k);
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
    public static Map<CacheManager, Set<String>> getManager(Class<?> clz) {
        return getManager(clz, Null.Str);
    }

    @NotNull
    public static Map<CacheManager, Set<String>> getManager(Class<?> claz, String method) {
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
            final String cr = op.getCacheResolver();
            final String cm = op.getCacheManager();
            final Set<String> cs = op.getCacheNames();
            top.initOperation(cr, cm, cs);
            mod.initOperation(cr, cm, cs);
        }
    }
}

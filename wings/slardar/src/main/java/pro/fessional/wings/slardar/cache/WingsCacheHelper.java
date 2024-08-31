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

    private static final Map<String, CacheManager> NameManager = new ConcurrentHashMap<>();
    private static final Map<CacheManager, Set<String>> ManagerName = new ConcurrentHashMap<>();
    private static CacheManager MemoryManager = null;
    private static CacheManager ServerManager = null;
    private static boolean helperPrepared = false;


    /**
     * Set CacheManager name and its Resolver
     */
    protected WingsCacheHelper(Map<String, CacheManager> mngs) {
        NameManager.putAll(mngs);

        MemoryManager = NameManager.get(WingsCache.Manager.Memory);
        ServerManager = NameManager.get(WingsCache.Manager.Server);

        ManagerName.clear();
        for (Map.Entry<String, CacheManager> en : NameManager.entrySet()) {
            ManagerName.computeIfAbsent(en.getValue(), k -> new HashSet<>())
                       .add(en.getKey());
        }
        helperPrepared = true;
    }

    /**
     * whether this helper is prepared
     */
    public static boolean isPrepared() {
        return helperPrepared;
    }

    @Nullable
    public static CacheManager getCacheManager(String name) {
        if (WingsCache.Manager.Memory.equalsIgnoreCase(name)) {
            return MemoryManager;
        }
        if (WingsCache.Manager.Server.equalsIgnoreCase(name)) {
            return ServerManager;
        }
        return NameManager.get(name);
    }

    public static Set<String> getManagerNames(CacheManager manage) {
        if (manage == null) return emptySet();
        return ManagerName.getOrDefault(manage, emptySet());
    }

    @Nullable
    public static Cache getCache(String manager, String cache) {
        final CacheManager cm = getCacheManager(manager);
        return cm == null ? null : cm.getCache(cache);
    }

    @NotNull
    public static Cache getMemoryCache(String name) {
        final Cache cache = MemoryManager.getCache(name);
        AssertState.notNull(cache, "memory cache is null, name={}", name);
        return cache;
    }

    @NotNull
    public static Cache getServerCache(String name) {
        final Cache cache = ServerManager.getCache(name);
        AssertState.notNull(cache, "server cache is null, name={}", name);
        return cache;
    }

    @NotNull
    public static CacheManager getMemory() {
        AssertState.notNull(MemoryManager, "Memory CacheManager is null");
        return MemoryManager;
    }

    @NotNull
    public static CacheManager getServer() {
        AssertState.notNull(ServerManager, "Server CacheManager is null");
        return ServerManager;
    }

    ///
    private static final Map<Class<?>, Map<String, Meta>> ClassCacheMeta = new ConcurrentHashMap<>();

    public static class Meta {
        private final Map<String, Set<String>> MngRlvCache = new HashMap<>();

        private final Map<CacheManager, Set<String>> managerName = new HashMap<>(); // lazy
        private final Map<String, Set<Cache>> managerCache = new HashMap<>(); // lazy

        public void initOperation(String cr, String cm, Set<String> cs) {
            if (!cr.isEmpty()) {
                MngRlvCache.computeIfAbsent(cr, k -> new HashSet<>()).addAll(cs);
            }
            if (!cm.isEmpty()) {
                MngRlvCache.computeIfAbsent(cm, k -> new HashSet<>()).addAll(cs);
            }
        }

        /**
         * CacheManager and its names
         */
        public Map<CacheManager, Set<String>> getManagers() {
            if (MngRlvCache.isEmpty()) return managerName;

            if (managerName.isEmpty()) {
                for (String nm : MngRlvCache.keySet()) {
                    final CacheManager m = getCacheManager(nm);
                    AssertState.notNull(m, "no CacheManager for {}", nm);
                    managerName.put(m, getManagerNames(m));
                }
            }
            return managerName;
        }

        /**
         * manager/resolver and its Caches
         */
        public Map<String, Set<Cache>> getCaches() {
            if (MngRlvCache.isEmpty()) return managerCache;

            if (managerCache.isEmpty()) {
                for (Map.Entry<String, Set<String>> en : MngRlvCache.entrySet()) {
                    String k = en.getKey();
                    final CacheManager m = getCacheManager(k);
                    AssertState.notNull(m, "no CacheManager for {}", k);
                    Set<Cache> st = new HashSet<>();
                    for (String c : en.getValue()) {
                        st.add(m.getCache(c));
                    }
                    managerCache.put(k, st);
                }
            }
            return managerCache;
        }
    }

    /**
     * manager/resolver name and its caches name
     */
    @NotNull
    public static Map<String, Set<String>> getCacheMeta(Class<?> clz) {
        return getCacheMeta(clz, Null.Str);
    }

    /**
     * manager/resolver name and its caches name
     */
    @NotNull
    public static Map<String, Set<String>> getCacheMeta(Class<?> claz, String method) {
        final Map<String, Meta> map = ClassCacheMeta.get(claz);
        if (map == null) return emptyMap();
        if (method == null) method = Null.Str;
        final Meta mt = map.get(method);
        return mt == null ? emptyMap() : mt.MngRlvCache;
    }

    /**
     * CacheManager and its names
     */
    @NotNull
    public static Map<CacheManager, Set<String>> getManager(Class<?> clz) {
        return getManager(clz, Null.Str);
    }

    /**
     * CacheManager and its names
     */
    @NotNull
    public static Map<CacheManager, Set<String>> getManager(Class<?> claz, String method) {
        final Map<String, Meta> map = ClassCacheMeta.get(claz);
        if (map == null) return emptyMap();
        if (method == null) method = Null.Str;
        final Meta mt = map.get(method);
        return mt == null ? emptyMap() : mt.getManagers();
    }

    /**
     * manager/resolver and its Caches
     */
    @NotNull
    public static Map<String, Set<Cache>> getCaches(Class<?> clz) {
        return getCaches(clz, Null.Str);
    }

    /**
     * manager/resolver and its Caches
     */
    @NotNull
    public static Map<String, Set<Cache>> getCaches(Class<?> claz, String method) {
        final Map<String, Meta> map = ClassCacheMeta.get(claz);
        if (map == null) return emptyMap();
        if (method == null) method = Null.Str;
        final Meta mt = map.get(method);
        return mt == null ? emptyMap() : mt.getCaches();
    }

    public static void prepareOperation(Method method, Collection<CacheOperation> opr) {
        if (opr == null || opr.isEmpty()) return;

        final Map<String, Meta> entry = ClassCacheMeta.computeIfAbsent(method.getDeclaringClass(), k -> new ConcurrentHashMap<>());
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

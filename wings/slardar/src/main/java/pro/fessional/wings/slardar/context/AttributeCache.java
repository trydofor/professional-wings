package pro.fessional.wings.slardar.context;

import lombok.Getter;
import lombok.Setter;
import org.cache2k.Cache;
import org.cache2k.config.CacheType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.best.TypedReg;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * thread safe
 *
 * @author trydofor
 * @since 2023-11-02
 */
public class AttributeCache<K, V> {

    private static final HashMap<TypedReg<?, ?>, Set<AttributeCache<?, ?>>> Registers = new HashMap<>();

    private final Cache<K, V> cache;
    @Setter
    private Function<K, V> loader;

    @Getter
    private final Class<?> owner;
    @Getter
    private final TypedReg<K, V> typed;
    @Getter
    private final String name;
    @Getter
    private final int size;
    @Getter
    private final int live;
    @Getter
    private final int idle;

    public AttributeCache(@NotNull Class<?> owner, @NotNull TypedReg<K, V> reg, int max, int ttl, int tti) {
        this(owner, reg, max, ttl, tti, null);
    }

    @SuppressWarnings("unchecked")
    public AttributeCache(@NotNull Class<?> owner, @NotNull TypedReg<K, V> reg, int max, int ttl, int tti, @Nullable Function<K, V> loader) {
        final String use = reg.regType.getName().substring(reg.regType.getPackageName().length() + 1);
        this.cache = (Cache<K, V>) WingsCache2k
                .builder(owner, use, max, ttl, tti)
                .keyType(CacheType.of(reg.keyType))
                .valueType(CacheType.of(reg.valType))
                .build();
        this.loader = loader;
        this.owner = owner;
        this.typed = reg;
        this.name = cache.getName();
        this.size = Math.max(0, max);
        this.live = Math.max(0, ttl);
        this.idle = Math.max(0, tti);
    }

    /**
     * Put key and value
     *
     * @param key   the key
     * @param value the value
     */
    public void putAttr(@NotNull K key, @NotNull V value) {
        cache.put(key, value);
    }

    /**
     * Put key and value with ttl
     *
     * @param key   the key
     * @param value the value
     * @param ttl   time to live in second
     */
    public void putAttr(@NotNull K key, @NotNull V value, int ttl) {
        if (ttl > 0) {
            cache.mutate(key, entry -> {
                entry.setValue(value);
                entry.setExpiryTime(entry.getStartTime() + ttl * 1000L);
            });
        }
        else {
            cache.put(key, value);
        }
    }

    /**
     * Put keys and values
     *
     * @param map map of key-value
     */
    public void putAttrs(@NotNull Map<K, V> map) {
        cache.putAll(map);
    }

    /**
     * Put keys and values with ttl
     *
     * @param map map of key-value
     * @param ttl time to live in second
     */
    public void putAttrs(@NotNull Map<K, V> map, int ttl) {
        if (ttl > 0) {
            for (Map.Entry<K, V> en : map.entrySet()) {
                cache.mutate(en.getKey(), entry -> {
                    entry.setValue(en.getValue());
                    entry.setExpiryTime(entry.getStartTime() + ttl * 1000L);
                });
            }
        }
        else {
            cache.putAll(map);
        }
    }

    /**
     * Try to get value by key, load it if not found, return `elze` if the result is null.
     *
     * @param key  unique key, e.g. userId
     * @param elze return `elze` if result is null
     */
    @Contract("_,!null->!null")
    public V tryAttr(@NotNull K key, V elze) {
        final V obj = tryAttr(key, false, 0);
        return obj == null ? elze : obj;
    }

    /**
     * Try to get value by key, load it if not found, return `elze` if the result is null.
     *
     * @param key  unique key, e.g. userId
     * @param elze return `elze` if result is null
     * @param ttl  ttl in second
     */
    @Contract("_,!null,_->!null")
    public V tryAttr(@NotNull K key, V elze, int ttl) {
        final V obj = tryAttr(key, false, ttl);
        return obj == null ? elze : obj;
    }

    /**
     * Try to get an attribute by typed key, load it if not found, throw NPE if the result is null.
     *
     * @param key unique key, e.g. userId
     */
    @NotNull
    public V tryAttr(@NotNull K key) {
        return tryAttr(key, true, 0);
    }

    /**
     * Try to get an attribute by typed key, load it if not found, throw NPE if the result is null.
     *
     * @param key unique key, e.g. userId
     * @param ttl ttl in second
     */
    @NotNull
    public V tryAttr(@NotNull K key, int ttl) {
        return tryAttr(key, true, ttl);
    }

    /**
     * Try to get an attribute by typed key, load it if not found, throw NPE if notnull and the result is null.
     *
     * @param key     unique key, e.g. userId
     * @param notnull whether notnull
     */
    @Contract("_,true ->!null")
    public V tryAttr(@NotNull K key, boolean notnull) {
        return tryAttr(key, notnull, 0);
    }

    /**
     * Try to get an attribute by typed key, load it if not found, throw NPE if notnull and the result is null.
     *
     * @param key     unique key, e.g. userId
     * @param notnull whether notnull
     * @param ttl     ttl in second
     */
    @Contract("_,true,_ ->!null")
    public V tryAttr(@NotNull K key, boolean notnull, int ttl) {

        final V rst = cache.invoke(key, entry -> {

            if (entry.exists()) {
                return entry.getValue();
            }

            V v = null;
            if (loader != null) {
                v = loader.apply(key);
                entry.setValue(v);
                if (ttl > 0) {
                    entry.setExpiryTime(entry.getStartTime() + ttl * 1000L);
                }
            }

            return v;
        });

        if (rst == null && notnull) {
            throw new NullPointerException("typed=" + typed + ",key=" + key);
        }
        else {
            return rst;
        }
    }

    /**
     * Get an attribute by typed key, and NOT load if not found.
     *
     * @param key unique key, e.g. userId
     */
    @Nullable
    public V getAttr(@NotNull K key) {
        return cache.get(key);
    }

    /**
     * Get all attributes by typed keys, and NOT load if not found.
     *
     * @param key unique key, e.g. userId
     */

    @NotNull
    public Map<K, V> getAttrs(@NotNull Collection<K> key) {
        return cache.getAll(key);
    }

    /**
     * remove an attribute by key
     *
     * @param key unique key, e.g. userId
     */
    public void ridAttr(K key) {
        cache.remove(key);
    }

    /**
     * remove all attribute by keys
     *
     * @param key unique key, e.g. userId
     */
    public void ridAttrs(Collection<? extends K> key) {
        if (key == null || key.isEmpty()) return;
        cache.removeAll(key);
    }

    /**
     * remove all attribute
     */
    public void ridAttrAll() {
        cache.removeAll();
    }


    /**
     * register to the global
     */
    public void register() {
        final Set<AttributeCache<?, ?>> tmp;
        synchronized (Registers) {
            tmp = Registers.computeIfAbsent(typed, ignore -> new LinkedHashSet<>());
        }
        synchronized (tmp) {
            tmp.add(this);
        }
    }

    /**
     * unregister from the global
     */
    public void unregister() {
        final Set<AttributeCache<?, ?>> tmp;
        synchronized (Registers) {
            tmp = Registers.get(typed);
        }

        if (tmp == null) return;

        synchronized (tmp) {
            tmp.remove(this);
        }

    }

    /**
     * thread safely list the registered types
     */
    @NotNull
    public static Set<TypedReg<?, ?>> registered() {
        final LinkedHashSet<TypedReg<?, ?>> tmp;
        synchronized (Registers) {
            tmp = new LinkedHashSet<>(Registers.keySet());
        }
        return tmp;
    }

    /**
     * thread safely loop the registered type
     */
    @SuppressWarnings("unchecked")
    public static <K, V> void forEach(@NotNull TypedReg<K, V> reg, @NotNull Consumer<AttributeCache<K, V>> handle) {
        final Set<AttributeCache<?, ?>> tmp;
        synchronized (Registers) {
            tmp = Registers.get(reg);
        }

        if (tmp == null) return;

        final LinkedHashSet<AttributeCache<?, ?>> ats;
        synchronized (tmp) {
            ats = new LinkedHashSet<>(tmp);
        }

        for (AttributeCache<?, ?> ac : ats) {
            handle.accept((AttributeCache<K, V>) ac);
        }
    }
}

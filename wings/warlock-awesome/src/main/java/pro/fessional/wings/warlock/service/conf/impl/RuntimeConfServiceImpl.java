package pro.fessional.wings.warlock.service.conf.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.best.AssertArgs;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.faceless.database.WingsTableCudHandler;
import pro.fessional.wings.faceless.database.WingsTableCudHandler.Cud;
import pro.fessional.wings.warlock.caching.CacheEventHelper;
import pro.fessional.wings.warlock.database.autogen.tables.WinConfRuntimeTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinConfRuntimeDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinConfRuntime;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;
import pro.fessional.wings.warlock.service.conf.RuntimeConfService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static pro.fessional.wings.warlock.caching.CacheConst.RuntimeConfService.CacheManager;
import static pro.fessional.wings.warlock.caching.CacheConst.RuntimeConfService.CacheName;
import static pro.fessional.wings.warlock.caching.CacheConst.RuntimeConfService.EventTables;
import static pro.fessional.wings.warlock.event.cache.TableChangeEvent.DELETE;
import static pro.fessional.wings.warlock.event.cache.TableChangeEvent.UPDATE;

/**
 * @author trydofor
 * @since 2022-03-09
 */
@Slf4j
@CacheConfig(cacheNames = CacheName, cacheManager = CacheManager)
public class RuntimeConfServiceImpl implements RuntimeConfService {

    public static final String PropHandler = "prop";
    public static final String JsonHandler = "json";
    public static final String KryoHandler = "kryo";

    @Setter(onMethod_ = {@Autowired})
    protected WinConfRuntimeDao winConfRuntimeDao;

    @Setter(onMethod_ = {@Autowired})
    protected WingsTableCudHandler wingsTableCudHandler;

    private final Map<String, ConversionService> handlerMap = new LinkedHashMap<>();

    public void addHandler(String type, ConversionService handler) {
        handlerMap.put(type, handler);
    }

    @Override
    public <T> T getObject(String key, TypeDescriptor type) {
        return selfLazy.getObjectCache(key, type);
    }

    @Override
    public void setObject(String key, Object value) {
        final WinConfRuntimeTable t = winConfRuntimeDao.getTable();

        final String handler = winConfRuntimeDao.fetchOne(String.class, t, t.Key.eq(key), t.Handler);
        ConversionService service = handlerMap.get(handler);
        final String str = service.convert(value, String.class);
        AssertArgs.notNull(str, "can not covert value to string, key={}", key);
        final int rc = winConfRuntimeDao
                .ctx()
                .update(t)
                .set(t.Current, str)
                .set(t.Previous, t.Current)
                .where(t.Key.eq(key))
                .execute();

        if (rc > 0) {
            wingsTableCudHandler.handle(this.getClass(), Cud.Update, t, () -> {
                Map<String, List<?>> field = new HashMap<>();
                field.put(t.Key.getName(), List.of(key));
                field.put(t.Current.getName(), List.of(str));
                return field;
            });
        }
    }

    @Override
    public boolean newObject(String key, Object value, String comment, String handler) {
        if (key == null || key.isEmpty() || value == null || handler == null) return false;
        ConversionService service = handlerMap.get(handler);
        if (service == null || !service.canConvert(value.getClass(), String.class)) return false;

        final String str = service.convert(value, String.class);
        AssertArgs.notNull(str, "can not covert value to string, key={}", key);
        final WinConfRuntime pojo = new WinConfRuntime();
        pojo.setKey(key);
        pojo.setCurrent(str);
        pojo.setPrevious(Null.Str);
        pojo.setInitial(str);
        pojo.setComment(StringUtils.trimToEmpty(comment));
        pojo.setHandler(handler);

        final int rc = winConfRuntimeDao.insertInto(pojo, false);
        log.debug("rc={}, key={}, han={}, val={}", rc, key, handler, str);

        if (rc > 0) {
            Cud type = rc == 1 ? Cud.Create : Cud.Update;
            final WinConfRuntimeTable t = winConfRuntimeDao.getTable();
            wingsTableCudHandler.handle(this.getClass(), type, t, () -> {
                Map<String, List<?>> field = new HashMap<>();
                field.put(t.Key.getName(), List.of(key));
                field.put(t.Current.getName(), List.of(str));
                return field;
            });
        }

        return rc >= 1;
    }

    @Override
    public boolean newObject(String key, Object value, String comment) {
        for (String handler : new ArrayList<>(handlerMap.keySet())) {
            if (newObject(key, value, comment, handler)) {
                return true;
            }
        }
        return false;
    }

    // cache self-invoke
    @Setter(onMethod_ = {@Autowired, @Lazy})
    protected RuntimeConfServiceImpl selfLazy;

    @Cacheable
    @SuppressWarnings("unchecked")
    public <T> T getObjectCache(String key, TypeDescriptor type) {
        if (winConfRuntimeDao.notTableExist()) return null;

        final WinConfRuntimeTable t = winConfRuntimeDao.getTable();
        final Record2<String, String> r2 = winConfRuntimeDao
                .ctx()
                .select(t.Current, t.Handler)
                .from(t)
                .where(t.Key.eq(key))
                .fetchOne();

        if (r2 != null) {
            ConversionService service = handlerMap.get(r2.value2());
            final Object obj = service.convert(r2.value1(), TypeDescriptor.valueOf(String.class), type);
            return (T) obj;
        }
        return null;
    }

    @EventListener
    @CacheEvict(allEntries = true, condition = "#result")
    public boolean evictAllConfCache(TableChangeEvent event) {
        final String tb = CacheEventHelper.receiveTable(event, EventTables, DELETE | UPDATE);
        if (tb != null) {
            log.debug("evictAllConfCache by {}, {}", tb, event);
            return true;
        }

        return false;
    }
}

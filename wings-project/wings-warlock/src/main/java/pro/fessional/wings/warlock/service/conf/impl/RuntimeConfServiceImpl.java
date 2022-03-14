package pro.fessional.wings.warlock.service.conf.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.warlock.database.autogen.tables.WinConfRuntimeTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinConfRuntimeDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinConfRuntime;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;
import pro.fessional.wings.warlock.service.conf.RuntimeConfService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-03-09
 */
@CacheConfig(cacheNames = WingsCache.Level.General + "RuntimeConfService")
@Slf4j
public class RuntimeConfServiceImpl implements RuntimeConfService {

    public static final String PropHandler = "prop";
    public static final String JsonHandler = "json";
    public static final String KryoHandler = "kryo";

    @Setter(onMethod_ = {@Autowired})
    private WinConfRuntimeDao winConfRuntimeDao;

    private final Map<String, ConversionService> handlerMap = new LinkedHashMap<>();

    public void addHandler(String type, ConversionService handler) {
        handlerMap.put(type, handler);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Cacheable(cacheManager = WingsCache.Manager.Memory)
    public <T> T getObject(String key, TypeDescriptor type) {
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

    @Override
    public void setObject(String key, Object value) {
        final WinConfRuntimeTable t = winConfRuntimeDao.getTable();

        final String handler = winConfRuntimeDao.fetchOne(String.class, t, t.Key.eq(key), t.Handler);
        ConversionService service = handlerMap.get(handler);
        final String str = service.convert(value, String.class);
        winConfRuntimeDao
                .ctx()
                .update(t)
                .set(t.Current, str)
                .set(t.Previous, t.Current)
                .where(t.Key.eq(key));
    }

    @Override
    public boolean newObject(String key, Object value, String comment, String handler) {
        if (key == null || key.isEmpty() || value == null || handler == null) return false;
        ConversionService service = handlerMap.get(handler);
        if (service == null || !service.canConvert(value.getClass(), String.class)) return false;

        final String str = service.convert(value, String.class);
        final WinConfRuntime pojo = new WinConfRuntime();
        pojo.setKey(key);
        pojo.setCurrent(str);
        pojo.setPrevious(Null.Str);
        pojo.setInitial(str);
        pojo.setComment(StringUtils.trimToEmpty(comment));
        pojo.setHandler(handler);

        final int rc = winConfRuntimeDao.insertInto(pojo, false);
        log.debug("rc={}, key={}, han={}, val={}", rc, key, handler, str);
        return rc >= 1;
    }

    @Override
    public void newObject(String key, Object value, String comment) {
        for (String handler : new ArrayList<>(handlerMap.keySet())) {
            if (newObject(key, value, comment, handler)) {
                return;
            }
        }
    }

    @EventListener
    @CacheEvict(allEntries = true, condition = "#result")
    public boolean evictAllConfCache(TableChangeEvent event) {
        if (event == null) {
            log.info("evict allEntries by NULL");
            return true;
        }
        else if (WinConfRuntimeTable.WinConfRuntime.getName().equalsIgnoreCase(event.getTable())) {
            log.info("evict allEntries by {}", event.getTable());
            return true;
        }
        return false;
    }

}

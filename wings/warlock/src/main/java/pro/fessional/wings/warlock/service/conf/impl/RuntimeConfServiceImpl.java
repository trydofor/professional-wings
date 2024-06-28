package pro.fessional.wings.warlock.service.conf.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.best.AssertArgs;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.faceless.database.WingsTableCudHandler;
import pro.fessional.wings.faceless.database.WingsTableCudHandler.Cud;
import pro.fessional.wings.silencer.enhance.ThisLazy;
import pro.fessional.wings.silencer.support.TypeSugar;
import pro.fessional.wings.warlock.caching.CacheEventHelper;
import pro.fessional.wings.warlock.database.autogen.tables.WinConfRuntimeTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinConfRuntimeDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinConfRuntime;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;
import pro.fessional.wings.warlock.service.conf.RuntimeConfService;

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
public class RuntimeConfServiceImpl extends ThisLazy<RuntimeConfServiceImpl> implements RuntimeConfService {

    public static final String PropHandler = "prop";
    public static final String JsonHandler = "json";
    public static final String KryoHandler = "kryo";

    @Setter(onMethod_ = { @Autowired })
    protected WinConfRuntimeDao winConfRuntimeDao;

    @Setter(onMethod_ = { @Autowired })
    protected WingsTableCudHandler wingsTableCudHandler;

    private final Map<String, ConversionService> handlerMap = new LinkedHashMap<>();

    public void putHandler(@NotNull String type, @NotNull ConversionService handler) {
        handlerMap.put(type, handler);
    }

    @Override
    public <T> T getObject(@NotNull String key, @NotNull TypeDescriptor type) {
        // dot not @Cacheable it but thisLazy, because,
        // (1) calling method inside make cache invalid
        // (2) recursive calling
        // (3) separating cache need copy dao and handlerMap
        return thisLazy.getObjectCache(key, type);
    }

    @Override
    public boolean setObject(@NotNull String key, @NotNull Object value) {
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
            wingsTableCudHandler.handle(this.getClass(), Cud.Update, t, field -> {
                field.put(t.Key.getName(), List.of(key));
                field.put(t.Current.getName(), List.of(str));
            });
        }

        return rc >= 1;
    }

    @Override
    public boolean newObject(@NotNull String key, @NotNull Object value, String comment, String handler, ResolvableType structs) {
        AssertArgs.notEmpty(key, "empty key");
        final Class<?> valClaz = value.getClass();

        ConversionService service = null;
        if (handler == null) { // auto select
            for (var en : handlerMap.entrySet()) {
                if (en.getValue().canConvert(valClaz, String.class)) {
                    service = en.getValue();
                    handler = en.getKey();
                    break;
                }
            }
        }
        else { // specified
            ConversionService cs = handlerMap.get(handler);
            if (cs.canConvert(valClaz, String.class)) {
                service = cs;
            }
        }

        if (service == null) return false;

        final String str = service.convert(value, String.class);
        AssertArgs.notNull(str, "can not covert value to string, key={}", key);

        if (structs == null) structs = ResolvableType.forClass(valClaz);

        final WinConfRuntime pojo = new WinConfRuntime();
        pojo.setKey(key);
        pojo.setEnabled(true);
        pojo.setCurrent(str);
        pojo.setPrevious(Null.Str);
        pojo.setInitial(str);
        pojo.setOutline(TypeSugar.outline(structs));
        pojo.setComment(StringUtils.trimToEmpty(comment));
        pojo.setHandler(handler);

        final int rc = winConfRuntimeDao.insertInto(pojo, false);
        log.info("newObject rc={}, key={}, han={}, val={}", rc, key, handler, str);

        if (rc > 0) {
            Cud type = rc == 1 ? Cud.Create : Cud.Update;
            final WinConfRuntimeTable t = winConfRuntimeDao.getTable();
            wingsTableCudHandler.handle(this.getClass(), type, t, field -> {
                field.put(t.Key.getName(), List.of(key));
                field.put(t.Current.getName(), List.of(str));
            });
        }

        return rc >= 1;
    }

    @Override
    public boolean enable(@NotNull String key, boolean enable) {
        WinConfRuntimeTable t = winConfRuntimeDao.getTable();
        final int rc = winConfRuntimeDao
            .ctx()
            .update(t)
            .set(t.Enabled, enable)
            .where(t.Key.eq(key))
            .execute();

        log.info("enable rc={}, key={}, enable={}", rc, key, enable);

        if (rc > 0) {
            wingsTableCudHandler.handle(this.getClass(), Cud.Update, t, field -> {
                field.put(t.Key.getName(), List.of(key));
                field.put(t.Enabled.getName(), List.of(enable));
            });
        }

        return rc >= 1;
    }

    @Cacheable
    @SuppressWarnings("unchecked")
    public <T> T getObjectCache(@NotNull String key, @NotNull TypeDescriptor type) {
        if (winConfRuntimeDao.notTableExist()) {
            log.warn("winConfRuntimeDao.notTableExist, key={}", key);
            return null;
        }

        final WinConfRuntimeTable t = winConfRuntimeDao.getTable();
        final Record2<String, String> r2 = winConfRuntimeDao
            .ctx()
            .select(t.Current, t.Handler)
            .from(t)
            .where(t.Key.eq(key))
            .and(t.Enabled.eq(true))
            .fetchOne();

        if (r2 != null) {
            ConversionService service = handlerMap.get(r2.value2());
            final Object obj = service.convert(r2.value1(), TypeSugar.StringDescriptor, type);
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

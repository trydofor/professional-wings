package pro.fessional.wings.warlock.service.perm.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pro.fessional.wings.warlock.database.autogen.tables.WinPermEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinPermEntryDao;
import pro.fessional.wings.warlock.service.perm.WarlockPermService;

import java.util.Map;

/**
 * @author trydofor
 * @since 2021-03-07
 */
@Service
@Slf4j
@CacheConfig(cacheNames = WarlockPermCacheListener.CacheName, cacheManager = WarlockPermCacheListener.ManagerName)
public class WarlockPermServiceImpl implements WarlockPermService {

    @Setter(onMethod_ = {@Autowired})
    private WinPermEntryDao winPermEntryDao;

    @Override
    @Cacheable
    public Map<Long, String> loadPermAll() {
        final WinPermEntryTable t = winPermEntryDao.getTable();

        final Map<Long, String> all = winPermEntryDao
                .ctx()
                .select(t.Id, t.Scopes.concat(".").concat(t.Action))
                .from(t)
                .where(t.onlyLiveData)
                .fetch()
                .intoMap(Record2::value1, Record2::value2);
        log.info("loadPermAll size={}", all.size());
        return all;
    }

    @CacheEvict
    public void evictPermAllCache() {
        log.info("evictPermAllCache");
    }
}

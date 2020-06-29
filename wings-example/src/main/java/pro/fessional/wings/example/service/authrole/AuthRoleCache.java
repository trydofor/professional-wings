package pro.fessional.wings.example.service.authrole;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pro.fessional.wings.example.database.autogen.tables.WinAuthRoleTable;
import pro.fessional.wings.example.database.autogen.tables.daos.WinAuthRoleDao;
import pro.fessional.wings.example.database.autogen.tables.pojos.WinAuthRole;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static pro.fessional.wings.slardar.spring.bean.WingsCacheConfiguration.LEVEL_GENERAL;
import static pro.fessional.wings.slardar.spring.bean.WingsCacheConfiguration.MANAGER_CAFFEINE;

@Service
@Setter(onMethod = @__({@Autowired}))
@CacheConfig(cacheNames = LEVEL_GENERAL + "AuthorityName", cacheManager = MANAGER_CAFFEINE)
public class AuthRoleCache {

    private LightIdService lightIdService;
    private WinAuthRoleDao winAuthRoleDao;

    @Cacheable(key = "#roleId")
    @NotNull
    public Map<Integer, String> loadAuth(long roleId) {
        WinAuthRoleTable t = winAuthRoleDao.getAliasForReader();
        Record1<String> r1 = winAuthRoleDao
                .dslContext()
                .select(t.AuthSet)
                .from(t)
                .where(t.Id.eq(roleId))
                .fetchOne();

        if (r1 == null) {
            return Collections.emptyMap();
        } else {
            String[] aids = StringUtils.split(r1.value1(), ",");
            Map<Integer, String> result = new HashMap<>();
            AuthEnumUtil.fillAuth(result, aids);
            return result;
        }
    }

    @CacheEvict(key = "#result")
    public long save(WinAuthRole authRole, JournalService.Journal journal) {
        long id;
        if (authRole.getId() == null) {
            id = lightIdService.getId(WinAuthRoleTable.class);
            authRole.setId(id);
            journal.commit(authRole);
            winAuthRoleDao.insert(authRole);
        } else {
            id = authRole.getId();
            journal.commit(authRole);
            winAuthRoleDao.update(authRole, true);
        }
        return id;
    }
}

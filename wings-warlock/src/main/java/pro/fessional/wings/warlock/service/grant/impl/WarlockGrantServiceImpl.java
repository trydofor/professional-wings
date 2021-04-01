package pro.fessional.wings.warlock.service.grant.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Record2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.slardar.context.GlobalAttributeHolder;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleGrantTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserGrantTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinRoleGrantDao;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserGrantDao;
import pro.fessional.wings.warlock.database.autogen.tables.records.WinRoleGrantRecord;
import pro.fessional.wings.warlock.database.autogen.tables.records.WinUserGrantRecord;
import pro.fessional.wings.warlock.enums.autogen.GrantType;
import pro.fessional.wings.warlock.service.grant.PermGrantHelper;
import pro.fessional.wings.warlock.service.grant.WarlockGrantService;
import pro.fessional.wings.warlock.service.perm.WarlockPermService;
import pro.fessional.wings.warlock.service.perm.WarlockRoleService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pro.fessional.wings.warlock.enums.autogen.GrantType.PERM;
import static pro.fessional.wings.warlock.enums.autogen.GrantType.ROLE;
import static pro.fessional.wings.warlock.service.user.WarlockUserAttribute.PermsByUid;
import static pro.fessional.wings.warlock.service.user.WarlockUserAttribute.RolesByUid;

/**
 * @author trydofor
 * @since 2021-03-26
 */
@Service
@Slf4j
public class WarlockGrantServiceImpl implements WarlockGrantService, InitializingBean {

    @Setter(onMethod_ = {@Autowired})
    private JournalService journalService;

    @Setter(onMethod_ = {@Autowired})
    private WinRoleGrantDao winRoleGrantDao;
    @Setter(onMethod_ = {@Autowired})
    private WinUserGrantDao winUserGrantDao;

    @Setter(onMethod_ = {@Autowired})
    private WarlockPermService warlockPermService;
    @Setter(onMethod_ = {@Autowired})
    private WarlockRoleService warlockRoleService;

    @Override
    public void grantRole(long refer, @NotNull GrantType type, @NotNull Set<Long> grant) {
        if (grant.isEmpty()) return;
        journalService.commit(Jane.Grant, refer, grant, commit -> {
            List<WinRoleGrantRecord> pos = new ArrayList<>(grant.size());
            for (Long g : grant) {
                WinRoleGrantRecord rd = new WinRoleGrantRecord();
                rd.setReferRole(refer);
                rd.setGrantType(type.getId());
                rd.setGrantEntry(g);
                commit.create(rd);
                pos.add(rd);
            }
            winRoleGrantDao.batchInsert(pos, 100, true);
        });
        GlobalAttributeHolder.ridAttrAll(RolesByUid, PermsByUid);
    }

    @Override
    public void purgeRole(long refer, @NotNull GrantType type, @NotNull Set<Long> grant) {
        if (grant.isEmpty()) return;
        journalService.commit(Jane.Purge, refer, grant, commit -> {
            final WinRoleGrantTable t = winRoleGrantDao.getTable();
            final Condition cond = t.ReferRole.eq(refer)
                                              .and(t.GrantType.eq(type.getId()))
                                              .and(t.GrantEntry.in(grant));
            winRoleGrantDao.delete(cond);
        });
        GlobalAttributeHolder.ridAttrAll(RolesByUid, PermsByUid);
    }


    @Override
    public void grantUser(long refer, @NotNull GrantType type, @NotNull Set<Long> grant) {
        if (grant.isEmpty()) return;
        journalService.commit(Jane.Grant, refer, grant, commit -> {
            List<WinUserGrantRecord> pos = new ArrayList<>(grant.size());
            for (Long g : grant) {
                WinUserGrantRecord rd = new WinUserGrantRecord();
                rd.setReferUser(refer);
                rd.setGrantType(type.getId());
                rd.setGrantEntry(g);
                commit.create(rd);
                pos.add(rd);
            }
            winUserGrantDao.batchInsert(pos, 100, true);
        });
        GlobalAttributeHolder.ridAttrAll(RolesByUid, PermsByUid);
    }

    @Override
    public void purgeUser(long refer, @NotNull GrantType type, @NotNull Set<Long> grant) {
        if (grant.isEmpty()) return;
        journalService.commit(Jane.Purge, refer, grant, commit -> {
            final WinUserGrantTable t = winUserGrantDao.getTable();
            final Condition cond = t.ReferUser.eq(refer)
                                              .and(t.GrantType.eq(type.getId()))
                                              .and(t.GrantEntry.in(grant));
            winUserGrantDao.delete(cond);
        });
        GlobalAttributeHolder.ridAttrAll(RolesByUid, PermsByUid);
    }

    @Override
    public void afterPropertiesSet() {
        log.info("warlock conf RolesByUid for GlobalAttributeHolder");
        GlobalAttributeHolder.regLoader(RolesByUid, key -> {
            final Long uid = key.getKey();
            final WinUserGrantTable t = winUserGrantDao.getTable();
            final Condition cond = t.GrantType.eq(ROLE.getId()).and(t.ReferUser.eq(uid));
            val userRoles = winUserGrantDao
                                    .ctx()
                                    .select(t.GrantEntry)
                                    .from(t)
                                    .where(cond)
                                    .fetchInto(Long.class);
            log.info("load {} roles for uid={}", userRoles.size(), uid);

            final Map<Long, String> roleAll = warlockRoleService.loadRoleAll();
            final Map<Long, Set<Long>> roleGrant = warlockRoleService.loadRoleGrant();
            Set<String> roles = new HashSet<>();
            for (Long rid : userRoles) {
                final Set<String> rs = PermGrantHelper.grantRole(rid, roleAll, roleGrant);
                log.info("grant {} roles for roleId={}", userRoles.size(), rid);
                roles.addAll(rs);
            }
            return roles;
        });

        log.info("warlock conf PermsByUid for GlobalAttributeHolder");
        GlobalAttributeHolder.regLoader(PermsByUid, key -> {
            final Long uid = key.getKey();
            final WinUserGrantTable tu = winUserGrantDao.getTable();
            final Condition cnd1 = tu.ReferUser.eq(uid);
            val userGrants = winUserGrantDao
                                     .ctx()
                                     .select(tu.GrantType, tu.GrantEntry)
                                     .from(tu)
                                     .where(cnd1)
                                     .fetch()
                                     .intoGroups(Record2::value1, Record2::value2);
            final Set<Long> pids = new HashSet<>(userGrants.getOrDefault(PERM.getId(), Collections.emptyList()));
            log.info("load {} perms for uid={}", pids.size(), uid);

            final List<Long> rids = userGrants.getOrDefault(ROLE.getId(), Collections.emptyList());
            log.info("load {} roles for uid={}", rids.size(), uid);

            if (!rids.isEmpty()) {
                final WinRoleGrantTable tr = winRoleGrantDao.getTable();
                final Condition cnd2 = tr.GrantType.eq(PERM.getId()).and(tr.ReferRole.in(rids));
                val perm2 = winRoleGrantDao
                                    .ctx()
                                    .select(tr.GrantEntry)
                                    .from(tr)
                                    .where(cnd2)
                                    .fetchInto(Long.class);
                log.info("load {} perms by roles for uid={}", perm2.size(), uid);
                pids.addAll(perm2);
            }

            final Map<Long, String> permAll = warlockPermService.loadPermAll();

            Set<String> perms = new HashSet<>();
            for (Long pid : pids) {
                final Set<String> ps = PermGrantHelper.inheritPerm(pid, permAll);
                for (String p : ps) {
                    // 去掉`*`权限
                    if (!p.contains(PermGrantHelper.ALL)) {
                        perms.add(p);
                    }
                }
            }
            return perms;
        });
    }
}

package pro.fessional.wings.warlock.service.user.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.data.Z;
import pro.fessional.wings.faceless.database.helper.DaoAssert;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.slardar.context.AttributeHolder;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.slardar.event.attr.AttributeRidEvent;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserBasisTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserBasisDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserBasis;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.errcode.CommonErrorEnum;
import pro.fessional.wings.warlock.service.user.WarlockUserBasisService;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static pro.fessional.wings.slardar.context.TerminalAttribute.LocaleByUid;
import static pro.fessional.wings.slardar.context.TerminalAttribute.ZoneIdByUid;
import static pro.fessional.wings.warlock.constants.WarlockGlobalAttribute.SaltByUid;

/**
 * @author trydofor
 * @since 2021-03-22
 */
@Slf4j
public class WarlockUserBasisServiceImpl implements WarlockUserBasisService, InitializingBean {

    @Setter(onMethod_ = {@Autowired})
    protected WinUserBasisDao winUserBasisDao;

    @Setter(onMethod_ = {@Autowired})
    protected LightIdService lightIdService;

    @Setter(onMethod_ = {@Autowired})
    protected JournalService journalService;

    @Override
    public void afterPropertiesSet() {
        if (winUserBasisDao.notTableExist()) return;

        log.info("warlock conf SaltByUid for GlobalAttributeHolder");
        AttributeHolder.regLoader(SaltByUid, k -> {
            final WinUserBasisTable t = winUserBasisDao.getTable();
            return winUserBasisDao.ctx()
                                  .select(t.Passsalt)
                                  .from(t)
                                  .where(t.Id.eq(k))
                                  .fetchOneInto(String.class);
        });

        log.info("warlock conf LocaleByUid for GlobalAttributeHolder");
        AttributeHolder.regLoader(LocaleByUid, k -> {
            final WinUserBasisTable t = winUserBasisDao.getTable();
            return winUserBasisDao.ctx()
                                  .select(t.Locale)
                                  .from(t)
                                  .where(t.Id.eq(k))
                                  .fetchOneInto(Locale.class);
        });

        log.info("warlock conf ZoneIdByUid for GlobalAttributeHolder");
        AttributeHolder.regLoader(ZoneIdByUid, k -> {
            final WinUserBasisTable t = winUserBasisDao.getTable();
            return winUserBasisDao.ctx()
                                  .select(t.Zoneid)
                                  .from(t)
                                  .where(t.Id.eq(k))
                                  .fetchOneInto(ZoneId.class);
        });
    }

    @Override
    public long create(@NotNull Basis user) {
        return journalService.submit(Jane.Create, user.getNickname(), commit -> {
            final WinUserBasisTable tu = winUserBasisDao.getTable();
            final long uid = lightIdService.getId(tu);
            WinUserBasis po = new WinUserBasis();
            po.setId(uid);
            po.setNickname(user.getNickname());

            final String passsalt = RandCode.human(40);
            AttributeHolder.putAttr(SaltByUid, uid, passsalt);
            po.setPasssalt(passsalt);

            po.setAvatar(Z.notNullSafe(Null.Str, user.getAvatar()));
            po.setGender(Z.notNullSafe(UserGender.UNKNOWN, user.getGender()));
            po.setLocale(Z.notNullSafe(TerminalContext::defaultLocale, user.getLocale()));
            po.setZoneid(Z.notNullSafe(TerminalContext::defaultZoneId, user.getZoneId()));
            po.setRemark(Z.notNullSafe(Null.Str, user.getRemark()));
            po.setStatus(Z.notNullSafe(UserStatus.UNINIT, user.getStatus()));
            commit.create(po);
            winUserBasisDao.insert(po);
            return uid;
        });
    }

    @Override
    public void modify(long userId, @NotNull Basis user) {
        final Integer rc = journalService.submit(Jane.Modify, userId, commit -> {
            final WinUserBasisTable tu = winUserBasisDao.getTable();
            Map<Field<?>, Object> setter = new HashMap<>();
            setter.put(tu.Nickname, user.getNickname());
            setter.put(tu.Gender, user.getGender());
            setter.put(tu.Avatar, user.getAvatar());
            setter.put(tu.Locale, user.getLocale());
            setter.put(tu.Zoneid, user.getZoneId());
            setter.put(tu.Remark, user.getRemark());
            setter.put(tu.Status, user.getStatus());
            // Must update, unless not found
            setter.put(tu.CommitId, commit.getCommitId());
            setter.put(tu.ModifyDt, commit.getCommitDt());
            return winUserBasisDao.update(tu, setter, tu.Id.eq(userId), true);
        });

        DaoAssert.assertEq1(rc, CommonErrorEnum.DataNotFound);

        AttributeRidEvent event = new AttributeRidEvent();
        event.rid(LocaleByUid, userId);
        event.rid(ZoneIdByUid, userId);
        EventPublishHelper.AsyncWidely.publishEvent(event);
    }
}

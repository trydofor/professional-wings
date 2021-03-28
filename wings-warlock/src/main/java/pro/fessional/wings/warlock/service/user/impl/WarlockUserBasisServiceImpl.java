package pro.fessional.wings.warlock.service.user.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.data.Z;
import pro.fessional.wings.faceless.database.helper.ModifyAssert;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserBasisTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserBasisDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserBasis;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.enums.errcode.CommonErrorEnum;
import pro.fessional.wings.warlock.service.user.WarlockUserBasisService;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-03-22
 */
@Slf4j
@Service
public class WarlockUserBasisServiceImpl implements WarlockUserBasisService {

    @Setter(onMethod_ = {@Autowired})
    private WinUserBasisDao winUserBasisDao;

    @Setter(onMethod_ = {@Autowired})
    private LightIdService lightIdService;

    @Setter(onMethod_ = {@Autowired})
    private JournalService journalService;

    @Override
    public long create(@NotNull User user) {
        return journalService.submit(Jane.Create, user.getNickname(), commit -> {
            final WinUserBasisTable tu = winUserBasisDao.getTable();
            final long uid = lightIdService.getId(tu);
            WinUserBasis po = new WinUserBasis();
            po.setId(uid);
            po.setNickname(user.getNickname());
            po.setAvatar(Z.notNull(user.getAvatar(), Null.Str));
            po.setGender(Z.notNull(user.getGender(), UserGender.UNKNOWN));
            po.setLocale(Z.notNull(user.getLocale(), Locale.getDefault()));
            po.setZoneid(Z.notNull(user.getZoneid(), ZoneId.systemDefault()));
            po.setRemark(Z.notNull(user.getRemark(), Null.Str));
            po.setStatus(Z.notNull(user.getStatus(), UserStatus.UNINIT));
            commit.create(po);
            winUserBasisDao.insert(po);
            return uid;
        });
    }

    @Override
    public void modify(long userId, @NotNull User user) {
        final Integer rc = journalService.submit(Jane.Modify, userId, commit -> {
            final WinUserBasisTable tu = winUserBasisDao.getTable();
            Map<Field<?>, Object> setter = new HashMap<>();
            setter.put(tu.Nickname, user.getNickname());
            setter.put(tu.Gender, user.getGender());
            setter.put(tu.Avatar, user.getAvatar());
            setter.put(tu.Locale, user.getLocale());
            setter.put(tu.Zoneid, user.getZoneid());
            setter.put(tu.Remark, user.getRemark());
            setter.put(tu.Status, user.getStatus());
            // 一定会更新，除非不存在
            setter.put(tu.CommitId, commit.getCommitId());
            setter.put(tu.ModifyDt, commit.getCommitDt());
            return winUserBasisDao.update(setter, tu.Id.eq(userId), true);
        });

        ModifyAssert.one(rc, CommonErrorEnum.DataNotFound);
    }
}

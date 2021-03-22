package pro.fessional.wings.warlock.service.user.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserBasicTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserBasicDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserBasic;
import pro.fessional.wings.warlock.service.user.WarlockUserBasicService;

/**
 * @author trydofor
 * @since 2021-03-22
 */
@Slf4j
@Service
public class WarlockUserBasicServiceImpl implements WarlockUserBasicService {

    @Setter(onMethod_ = {@Autowired})
    private WinUserBasicDao winUserBasicDao;

    @Setter(onMethod_ = {@Autowired})
    private LightIdService lightIdService;

    @Setter(onMethod_ = {@Autowired})
    private JournalService journalService;

    @Override
    public long createUser(User dto) {
        return journalService.submit(Jane.CreateUser, dto.getNickname(), commit -> {
            final WinUserBasicTable tu = winUserBasicDao.getTable();
            final long uid = lightIdService.getId(tu);
            WinUserBasic user = new WinUserBasic();
            user.setId(uid);
            user.setNickname(dto.getNickname());
            user.setAvatar(dto.getAvatar());
            user.setGender(dto.getGender());
            user.setLocale(dto.getLocale());
            user.setZoneid(dto.getZoneid());
            user.setRemark(dto.getRemark());
            user.setStatus(dto.getStatus());
            commit.create(user);
            winUserBasicDao.insert(user);
            return uid;
        });
    }
}

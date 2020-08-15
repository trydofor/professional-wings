package pro.fessional.wings.example.service.user.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.example.database.autogen.tables.WinUserTable;
import pro.fessional.wings.example.database.autogen.tables.daos.WinUserDao;
import pro.fessional.wings.example.database.autogen.tables.pojos.WinUser;
import pro.fessional.wings.example.enums.auto.UserStatus;
import pro.fessional.wings.example.service.user.UserService;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;

/**
 * @author trydofor
 * @since 2020-07-01
 */
@Service
@Setter(onMethod = @__({@Autowired}))
@Slf4j
public class UserServiceImpl implements UserService {

    private WinUserDao winUserDao;
    private LightIdService lightIdService;
    private JournalService journalService;

    @Override
    public R<Long> create(UserCreate user) {

        long id = lightIdService.getId(WinUserTable.class);
        JournalService.Journal journal = journalService.commit(UserService.class);

        WinUser po = new WinUser();
        po.setId(id);
        po.setName(user.getName());
        po.setGender(user.getGender());
        po.setBirth(user.getBirth());
        po.setAvatar(user.getAvatar());
        po.setCountry(user.getCountry());
        po.setLanguage(user.getLanguage());
        po.setTimezone(user.getTimezone());
        po.setAuthSet(EmptyValue.VARCHAR);
        po.setRoleSet(EmptyValue.VARCHAR);
        po.setStatus(UserStatus.UNINIT.getId());
        journal.commit(po);

        winUserDao.insert(po);
        return R.okData(id);
    }
}

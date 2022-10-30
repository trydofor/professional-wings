package pro.fessional.wings.warlock.service.user;


import pro.fessional.mirana.best.TypedReg;

import java.util.Set;

/**
 * @author trydofor
 * @since 2021-03-31
 */
public interface WarlockUserAttribute {

    TypedReg<Long, String> SaltByUid = new TypedReg<>() {};
    TypedReg<Long, Set<String>> PermsByUid = new TypedReg<>() {};
    TypedReg<Long, Set<String>> RolesByUid = new TypedReg<>() {};
}

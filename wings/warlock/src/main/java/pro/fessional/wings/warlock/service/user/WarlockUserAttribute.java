package pro.fessional.wings.warlock.service.user;

import pro.fessional.wings.slardar.context.GlobalAttributeHolder.Reg;

import java.util.Set;

/**
 * @author trydofor
 * @since 2021-03-31
 */
public interface WarlockUserAttribute {

    Reg<Long, String> SaltByUid = new Reg<>() {};
    Reg<Long, Set<String>> PermsByUid = new Reg<>() {};
    Reg<Long, Set<String>> RolesByUid = new Reg<>() {};
}

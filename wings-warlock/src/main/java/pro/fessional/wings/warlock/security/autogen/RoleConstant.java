package pro.fessional.wings.warlock.security.autogen;

/**
 * @since 2021-05-31
 */
public interface RoleConstant {

    /**
     * id=1, remark=超级管理员，全部权限
     */
    String ROOT = "ROOT";
    long ID$ROOT = 1;
    /**
     * id=1, remark=超级管理员，全部权限, prefix=ROLE_
     */
    String ROLE$ROOT = "ROLE_ROOT";

    /**
     * id=9, remark=系统管理员，系统权限
     */
    String SYSTEM = "SYSTEM";
    long ID$SYSTEM = 9;
    /**
     * id=9, remark=系统管理员，系统权限, prefix=ROLE_
     */
    String ROLE$SYSTEM = "ROLE_SYSTEM";

    /**
     * id=10, remark=普通管理员，业务权限
     */
    String ADMIN = "ADMIN";
    long ID$ADMIN = 10;
    /**
     * id=10, remark=普通管理员，业务权限, prefix=ROLE_
     */
    String ROLE$ADMIN = "ROLE_ADMIN";
}
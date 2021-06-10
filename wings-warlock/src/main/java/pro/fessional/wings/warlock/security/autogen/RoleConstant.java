package pro.fessional.wings.warlock.security.autogen;

/**
 * @since 2021-06-08
 */
public interface RoleConstant {

    /**
     * prefix=ROLE_
     */
    String $PREFIX = "ROLE_";


    /**
     * id=1, remark=超级管理员，全部权限
     */
    String ROOT = "ROOT";
    long ID$ROOT = 1;
    String ROLE$ROOT = "ROLE_ROOT";

    /**
     * id=9, remark=系统管理员，系统权限
     */
    String SYSTEM = "SYSTEM";
    long ID$SYSTEM = 9;
    String ROLE$SYSTEM = "ROLE_SYSTEM";

    /**
     * id=10, remark=普通管理员，业务权限
     */
    String ADMIN = "ADMIN";
    long ID$ADMIN = 10;
    String ROLE$ADMIN = "ROLE_ADMIN";
}
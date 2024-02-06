package pro.fessional.wings.warlock.security.autogen;

import javax.annotation.processing.Generated;

/**
 * @since 2024-02-05
 */
@Generated("wings faceless codegen")
public interface RoleConstant {

    /**
     * prefix=ROLE_
     */
    String $PREFIX = "ROLE_";


    /**
     * id=1, remark=Super Admin, full privileges
     */
    String ROOT = "ROOT";
    long ID$ROOT = 1;
    String ROLE$ROOT = "ROLE_ROOT";

    /**
     * id=9, remark=System Admin, system privileges
     */
    String SYSTEM = "SYSTEM";
    long ID$SYSTEM = 9;
    String ROLE$SYSTEM = "ROLE_SYSTEM";

    /**
     * id=10, remark=Normal Admin, business privileges
     */
    String ADMIN = "ADMIN";
    long ID$ADMIN = 10;
    String ROLE$ADMIN = "ROLE_ADMIN";
}
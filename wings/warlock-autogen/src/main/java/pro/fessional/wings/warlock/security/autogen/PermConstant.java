package pro.fessional.wings.warlock.security.autogen;

/**
 * @since 2023-09-08
 */
public interface PermConstant {

    interface System {

        interface Perm {

            /**
             * id=21, remark=create perm
             */
            String create = "system.perm.create";
            long ID$create = 21;

            /**
             * id=22, remark=update perm
             */
            String update = "system.perm.update";
            long ID$update = 22;

            /**
             * id=23, remark=delete perm
             */
            String delete = "system.perm.delete";
            long ID$delete = 23;

            /**
             * id=24, remark=assign perm to user/role
             */
            String assign = "system.perm.assign";
            long ID$assign = 24;
        }

        interface Role {

            /**
             * id=31, remark=create role
             */
            String create = "system.role.create";
            long ID$create = 31;

            /**
             * id=32, remark=update role
             */
            String update = "system.role.update";
            long ID$update = 32;

            /**
             * id=33, remark=delete role
             */
            String delete = "system.role.delete";
            long ID$delete = 33;

            /**
             * id=34, remark=assign role to user/role
             */
            String assign = "system.role.assign";
            long ID$assign = 34;
        }

        interface User {

            /**
             * id=11, remark=create user
             */
            String create = "system.user.create";
            long ID$create = 11;

            /**
             * id=12, remark=update user
             */
            String update = "system.user.update";
            long ID$update = 12;

            /**
             * id=13, remark=delete user
             */
            String delete = "system.user.delete";
            long ID$delete = 13;
        }
    }
}
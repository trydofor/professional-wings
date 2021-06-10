package pro.fessional.wings.warlock.security.autogen;

/**
 * @since 2021-06-08
 */
public interface PermConstant {

    interface System {

        interface Perm {

            /**
             * id=21, remark=权限创建
             */
            String create = "system.perm.create";
            long ID$create = 21;

            /**
             * id=22, remark=权限编辑
             */
            String update = "system.perm.update";
            long ID$update = 22;

            /**
             * id=23, remark=权限删除
             */
            String delete = "system.perm.delete";
            long ID$delete = 23;

            /**
             * id=24, remark=角色指派，给用户或角色
             */
            String assign = "system.perm.assign";
            long ID$assign = 24;
        }

        interface Role {

            /**
             * id=31, remark=角色创建
             */
            String create = "system.role.create";
            long ID$create = 31;

            /**
             * id=32, remark=角色编辑
             */
            String update = "system.role.update";
            long ID$update = 32;

            /**
             * id=33, remark=角色删除
             */
            String delete = "system.role.delete";
            long ID$delete = 33;

            /**
             * id=34, remark=角色指派，给用户或角色
             */
            String assign = "system.role.assign";
            long ID$assign = 34;
        }

        interface User {

            /**
             * id=11, remark=用户创建
             */
            String create = "system.user.create";
            long ID$create = 11;

            /**
             * id=12, remark=用户编辑
             */
            String update = "system.user.update";
            long ID$update = 12;

            /**
             * id=13, remark=用户删除
             */
            String delete = "system.user.delete";
            long ID$delete = 13;
        }
    }
}
package pro.fessional.wings.warlock.security.autogen;

/**
 * @since 2021-03-27
 */
public interface PermConstant {


    interface System {

        interface Perm {

            /**
             * id=21, remark=权限创建
             */
            String create = "system.perm.create";

            /**
             * id=22, remark=权限编辑
             */
            String update = "system.perm.update";

            /**
             * id=23, remark=权限删除
             */
            String delete = "system.perm.delete";

            /**
             * id=24, remark=角色指派，给用户或角色
             */
            String assign = "system.perm.assign";

        }
        interface Role {

            /**
             * id=31, remark=角色创建
             */
            String create = "system.role.create";

            /**
             * id=32, remark=角色编辑
             */
            String update = "system.role.update";

            /**
             * id=33, remark=角色删除
             */
            String delete = "system.role.delete";

            /**
             * id=34, remark=角色指派，给用户或角色
             */
            String assign = "system.role.assign";

        }
        interface User {

            /**
             * id=11, remark=用户创建
             */
            String create = "system.user.create";

            /**
             * id=12, remark=用户编辑
             */
            String update = "system.user.update";

            /**
             * id=13, remark=用户删除
             */
            String delete = "system.user.delete";

        }
    }
}
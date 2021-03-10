package pro.fessional.wings.warlock.security.autogen;

/**
 * @since 2021-03-05
 */
public interface PermConstant {


    interface System {

        interface Perm {

            /**
             * id=21, remark=读取
             */
            String read = "system.perm.read";

            /**
             * id=22, remark=搜索
             */
            String search = "system.perm.search";

            /**
             * id=23, remark=创建
             */
            String create = "system.perm.create";

            /**
             * id=24, remark=编辑
             */
            String update = "system.perm.update";

            /**
             * id=25, remark=删除
             */
            String delete = "system.perm.delete";

        }
        interface Role {

            /**
             * id=31, remark=读取
             */
            String read = "system.role.read";

            /**
             * id=32, remark=搜索
             */
            String search = "system.role.search";

            /**
             * id=33, remark=创建
             */
            String create = "system.role.create";

            /**
             * id=34, remark=编辑
             */
            String update = "system.role.update";

            /**
             * id=35, remark=删除
             */
            String delete = "system.role.delete";

        }
        interface User {

            /**
             * id=11, remark=读取
             */
            String read = "system.user.read";

            /**
             * id=12, remark=搜索
             */
            String search = "system.user.search";

            /**
             * id=13, remark=创建
             */
            String create = "system.user.create";

            /**
             * id=14, remark=编辑
             */
            String update = "system.user.update";

            /**
             * id=15, remark=删除
             */
            String delete = "system.user.delete";

        }
    }
}
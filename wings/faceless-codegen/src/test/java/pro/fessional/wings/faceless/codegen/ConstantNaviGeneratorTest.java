package pro.fessional.wings.faceless.codegen;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.faceless.codegen.ConstantNaviGenerator.Entry;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author trydofor
 * @since 2021-03-05
 */
@Disabled("Mock permission tree, generate Constant, use db instead")
class ConstantNaviGeneratorTest {

    @Test
    void generate() {
        Collection<Entry> entries = new ArrayList<>();
        entries.add(create(1L, "root", "顶级权限，不对外使用"));
        entries.add(create(1L, ".*", "顶级权限，不对外使用"));
        entries.add(create(1L, ".read", "顶级权限，read"));
        entries.add(create(10L, "system.user.*", "用户全部"));
        entries.add(create(11L, "system.user.read", "用户读取"));
        entries.add(create(12L, "system.user.search", "用户搜索"));
        entries.add(create(21L, "system.perm.read", "权限读取"));
        entries.add(create(22L, "system.perm.search", "权限搜索"));
        entries.add(create(31L, "system.menu.navi.read", "菜单导航读取"));
        entries.add(create(32L, "system.menu.navi.search", "菜单导航搜索"));


        ConstantNaviGenerator generator = new ConstantNaviGenerator();
        generator.setTargetDir("src/test/java");
        generator.setPackageName("pro.fessional.wings.faceless.autogen");
        generator.generate("PermConstant", "", entries);
        generator.generate("RoleConstant", "ROLE_", entries);
    }


    private Entry create(long id, String name, String remark){
        Entry en = new Entry();
        en.setId(id);
        en.setName(name);
        en.setRemark(remark);
        return en;
    }
}

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
        entries.add(create(1L, "root", "super privilege, NOT for external use"));
        entries.add(create(1L, ".*", "super privilege, NOT for external use"));
        entries.add(create(1L, ".read", "super privilege, read"));
        entries.add(create(10L, "system.user.*", "all"));
        entries.add(create(11L, "system.user.read", "read user"));
        entries.add(create(12L, "system.user.search", "search user"));
        entries.add(create(21L, "system.perm.read", "read perm"));
        entries.add(create(22L, "system.perm.search", "search perm"));
        entries.add(create(31L, "system.menu.navi.read", "read menu navi"));
        entries.add(create(32L, "system.menu.navi.search", "search menu navi"));


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

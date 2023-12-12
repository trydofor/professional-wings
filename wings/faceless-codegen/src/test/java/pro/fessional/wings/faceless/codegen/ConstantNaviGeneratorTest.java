package pro.fessional.wings.faceless.codegen;

import io.qameta.allure.TmsLink;
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
    @TmsLink("C12018")
    void generate() {
        Collection<Entry> entries = new ArrayList<>();
        entries.add(create(1000L, "root", "super privilege, NOT for external use"));
        entries.add(create(1000L, ".*", "super privilege, NOT for external use"));
        entries.add(create(1000L, ".read", "super privilege, read"));
        entries.add(create(10000L, "system.user.*", "all"));
        entries.add(create(10001L, "system.user.read", "read user"));
        entries.add(create(10002L, "system.user.search", "search user"));
        entries.add(create(20001L, "system.perm.read", "read perm"));
        entries.add(create(20002L, "system.perm.search", "search perm"));
        entries.add(create(30001L, "system.menu.navi.read", "read menu navi"));
        entries.add(create(30002L, "system.menu.navi.search", "search menu navi"));


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

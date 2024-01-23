package pro.fessional.wings.warlock.project;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.faceless.codegen.JdbcDataLoadHelper;

import static pro.fessional.wings.warlock.project.TestWarlock0CodegenConstant.JAVA;
import static pro.fessional.wings.warlock.project.TestWarlock0CodegenConstant.JDBC;
import static pro.fessional.wings.warlock.project.TestWarlock0CodegenConstant.PASS;
import static pro.fessional.wings.warlock.project.TestWarlock0CodegenConstant.USER;

/**
 * @author trydofor
 * @since 2021-02-22
 */
@Disabled("Sample: Code generation, managed by devops")
class TestWarlock2CodeGeneratorSample {

    @Test
    @TmsLink("C14021")
    public void genEnum() {
        Warlock2EnumGenerator generator = new Warlock2EnumGenerator();
        generator.setTargetDir(JAVA);
        generator.gen(JDBC, USER, PASS,
                Warlock2EnumGenerator.excludeStandard());
    }

    @Test
    @TmsLink("C14022")
    public void genJooq() {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(JAVA);
        generator.gen(JDBC, USER, PASS,
                Warlock3JooqGenerator.includeWarlockBase(false),
                Warlock3JooqGenerator.includeWarlockBond(true),
                bd -> bd.setGlobalSuffix("Warlock"));
    }

    @Test
    @TmsLink("C14023")
    public void genAuth() {
        JdbcDataLoadHelper helper = JdbcDataLoadHelper.use(JDBC, USER, PASS);
        Warlock4AuthGenerator generator = new Warlock4AuthGenerator();
        generator.setTargetDir(JAVA);
        generator.genPerm(helper);
        generator.genRole(helper);
    }
}

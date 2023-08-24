package pro.fessional.wings.warlock.project;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.faceless.codegen.JdbcDataLoadHelper;

import static pro.fessional.wings.warlock.project.Warlock0CodegenConstant.JAVA;
import static pro.fessional.wings.warlock.project.Warlock0CodegenConstant.JDBC;
import static pro.fessional.wings.warlock.project.Warlock0CodegenConstant.PASS;
import static pro.fessional.wings.warlock.project.Warlock0CodegenConstant.USER;

/**
 * @author trydofor
 * @since 2021-02-22
 */
@Disabled("Code generation, managed by devops")
class Warlock2CodeGeneratorTest {

    @Test
    public void genEnum() {
        Warlock2EnumGenerator generator = new Warlock2EnumGenerator();
        generator.setTargetDir(JAVA);
        generator.gen(JDBC, USER, PASS,
                Warlock2EnumGenerator.excludeStandard());
    }

    @Test
    public void genJooq() {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(JAVA);
        generator.gen(JDBC, USER, PASS,
                Warlock3JooqGenerator.includeWarlockBase(false),
                Warlock3JooqGenerator.includeWarlockBond(true),
                bd -> bd.setGlobalSuffix("Warlock"));
    }

    @Test
    public void genAuth() {
        JdbcDataLoadHelper helper = JdbcDataLoadHelper.use(JDBC, USER, PASS);
        Warlock4AuthGenerator generator = new Warlock4AuthGenerator();
        generator.setTargetDir(JAVA);
        generator.genPerm(helper);
        generator.genRole(helper);
    }
}

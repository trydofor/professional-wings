package pro.fessional.wings.warlock.project;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static pro.fessional.wings.warlock.project.Warlock0CodegenConstant.JAVA;
import static pro.fessional.wings.warlock.project.Warlock0CodegenConstant.JDBC;
import static pro.fessional.wings.warlock.project.Warlock0CodegenConstant.PASS;
import static pro.fessional.wings.warlock.project.Warlock0CodegenConstant.USER;

/**
 * @author trydofor
 * @since 2021-02-22
 */
@Disabled("手动执行")
class Warlock2CodeGeneratorTest {

    @Test
    public void genJooq() {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(JAVA);
        generator.gen(JDBC, USER, PASS,
                Warlock3JooqGenerator.includeWarlockBond(),
                bd -> bd.setGlobalSuffix("WarlockBond"));

    }
}

package com.moilioncircle.wings.devops.project;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.faceless.codegen.JdbcDataLoadHelper;
import pro.fessional.wings.warlock.project.Warlock2EnumGenerator;
import pro.fessional.wings.warlock.project.Warlock3JooqGenerator;
import pro.fessional.wings.warlock.project.Warlock4AuthGenerator;

import static com.moilioncircle.wings.devops.project.Devops0ProjectConstant.JAVA_MAIN;
import static com.moilioncircle.wings.devops.project.Devops0ProjectConstant.JDBC_PASS;
import static com.moilioncircle.wings.devops.project.Devops0ProjectConstant.JDBC_URL;
import static com.moilioncircle.wings.devops.project.Devops0ProjectConstant.JDBC_USER;
import static com.moilioncircle.wings.devops.project.Devops0ProjectConstant.PKG_AUTH;
import static com.moilioncircle.wings.devops.project.Devops0ProjectConstant.PKG_ENUM;
import static com.moilioncircle.wings.devops.project.Devops0ProjectConstant.PKG_JOOQ;

/**
 * @author trydofor
 * @since 2021-02-22
 */
@Disabled("Code Gen")
public class Devops2CodeGeneratorTest {

    @Test
    public void genEnum() {
        Warlock2EnumGenerator generator = new Warlock2EnumGenerator();
        generator.setTargetDir(JAVA_MAIN);
        generator.setTargetPkg(PKG_ENUM);
        generator.gen(JDBC_URL, JDBC_USER, JDBC_PASS,
                Warlock2EnumGenerator.excludeStandard(),
                Warlock2EnumGenerator.excludeWarlock());
    }

    @Test
    public void genJooq() {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(JAVA_MAIN);
        generator.setTargetPkg(PKG_JOOQ);
        generator.gen(JDBC_URL, JDBC_USER, JDBC_PASS,
                builder -> builder.databaseIncludes("winx_.*")
//                        .forcedIntConsEnum(ScanStatus.class, ".*\\.scan_status")
//                        .forcedIntConsEnum(DeliveryType.class, ".*\\.delivery_type")
//                        .forcedIntConsEnum(WarehouseType.class, "owt_warehouse\\.type")
        );
    }

    @Test
    public void genAuth() {
        final JdbcDataLoadHelper helper = JdbcDataLoadHelper
                .use(JDBC_URL, JDBC_USER, JDBC_PASS);
        Warlock4AuthGenerator generator = new Warlock4AuthGenerator();
        generator.setTargetDir(JAVA_MAIN);
        generator.setTargetPkg(PKG_AUTH);
        generator.genPerm(helper);
        generator.genRole(helper);
    }
}

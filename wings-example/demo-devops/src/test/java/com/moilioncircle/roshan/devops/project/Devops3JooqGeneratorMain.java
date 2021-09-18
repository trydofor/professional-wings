package com.moilioncircle.roshan.devops.project;


import pro.fessional.wings.warlock.project.Warlock3JooqGenerator;

import static com.moilioncircle.roshan.devops.project.Devops0ProjectConstant.JAVA_MAIN;
import static com.moilioncircle.roshan.devops.project.Devops0ProjectConstant.JDBC_PASS;
import static com.moilioncircle.roshan.devops.project.Devops0ProjectConstant.JDBC_URL;
import static com.moilioncircle.roshan.devops.project.Devops0ProjectConstant.JDBC_USER;
import static com.moilioncircle.roshan.devops.project.Devops0ProjectConstant.PKG_JOOQ;

/**
 * @author trydofor
 * @since 2021-02-22
 */
public class Devops3JooqGeneratorMain {

    public static void main(String[] args) {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(JAVA_MAIN);
        generator.setTargetPkg(PKG_JOOQ);
        generator.gen(JDBC_URL, JDBC_USER, JDBC_PASS,
                builder -> builder.databaseIncludes("demo_.*")
//                        .forcedIntConsEnum(ScanStatus.class, ".*\\.scan_status")
//                        .forcedIntConsEnum(DeliveryType.class, ".*\\.delivery_type")
//                        .forcedIntConsEnum(WarehouseType.class, "owt_warehouse\\.type")
        );
    }
}

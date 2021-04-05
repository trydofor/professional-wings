package com.moilioncircle.roshan.common.project;

import pro.fessional.wings.faceless.codegen.JdbcDataLoadHelper;
import pro.fessional.wings.warlock.project.Warlock4AuthGenerator;

import static com.moilioncircle.roshan.common.project.Demo0ProjectConstant.JAVA_MAIN;
import static com.moilioncircle.roshan.common.project.Demo0ProjectConstant.JDBC_PASS;
import static com.moilioncircle.roshan.common.project.Demo0ProjectConstant.JDBC_URL;
import static com.moilioncircle.roshan.common.project.Demo0ProjectConstant.JDBC_USER;
import static com.moilioncircle.roshan.common.project.Demo0ProjectConstant.PKG_AUTH;

/**
 * @author trydofor
 * @since 2021-02-22
 */
public class Demo4AuthGeneratorMain {

    public static void main(String[] args) {
        final JdbcDataLoadHelper helper = JdbcDataLoadHelper
                                                  .use(JDBC_URL, JDBC_USER, JDBC_PASS);
        Warlock4AuthGenerator generator = new Warlock4AuthGenerator();
        generator.setTargetDir(JAVA_MAIN);
        generator.setTargetPkg(PKG_AUTH);
        generator.genPerm(helper);
        generator.genRole(helper);
    }
}

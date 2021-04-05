package com.moilioncircle.roshan.common.project;

import pro.fessional.wings.warlock.project.Warlock2EnumGenerator;

import static com.moilioncircle.roshan.common.project.Demo0ProjectConstant.JAVA_MAIN;
import static com.moilioncircle.roshan.common.project.Demo0ProjectConstant.JDBC_PASS;
import static com.moilioncircle.roshan.common.project.Demo0ProjectConstant.JDBC_URL;
import static com.moilioncircle.roshan.common.project.Demo0ProjectConstant.JDBC_USER;
import static com.moilioncircle.roshan.common.project.Demo0ProjectConstant.PKG_ENUM;

/**
 * @author trydofor
 * @since 2021-02-22
 */
public class Demo2EnumGeneratorMain {

    public static void main(String[] args) {
        Warlock2EnumGenerator generator = new Warlock2EnumGenerator();
        generator.setTargetDir(JAVA_MAIN);
        generator.setTargetPkg(PKG_ENUM);
        generator.gen(JDBC_URL, JDBC_USER, JDBC_PASS,
                Warlock2EnumGenerator.excludeStandard(),
                Warlock2EnumGenerator.excludeWarlock());
    }
}

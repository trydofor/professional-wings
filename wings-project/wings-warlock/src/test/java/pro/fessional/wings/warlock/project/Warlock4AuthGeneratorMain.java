package pro.fessional.wings.warlock.project;

import pro.fessional.wings.faceless.codegen.JdbcDataLoadHelper;

/**
 * @author trydofor
 * @since 2021-02-22
 */
class Warlock4AuthGeneratorMain {

    public static void main(String[] args) {
        JdbcDataLoadHelper helper = JdbcDataLoadHelper.use(Warlock0CodegenConstant.JDBC, Warlock0CodegenConstant.USER, Warlock0CodegenConstant.PASS);
        Warlock4AuthGenerator generator = new Warlock4AuthGenerator();
        generator.setTargetDir(Warlock0CodegenConstant.JAVA);
        generator.genPerm(helper);
        generator.genRole(helper);
    }
}

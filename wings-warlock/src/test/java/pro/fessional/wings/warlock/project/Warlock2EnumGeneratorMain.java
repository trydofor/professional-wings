package pro.fessional.wings.warlock.project;

/**
 * @author trydofor
 * @since 2021-02-22
 */
class Warlock2EnumGeneratorMain {

    public static void main(String[] args) {
        Warlock2EnumGenerator generator = new Warlock2EnumGenerator();
        generator.setTargetDir(Warlock0CodegenConstant.JAVA);
        generator.gen(Warlock0CodegenConstant.JDBC, Warlock0CodegenConstant.USER, Warlock0CodegenConstant.PASS);
    }
}

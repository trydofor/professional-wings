package pro.fessional.wings.warlock.project;

/**
 * @author trydofor
 * @since 2021-02-22
 */
class Warlock3JooqGeneratorMain {

    public static void main(String[] args) {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(Warlock0CodegenConstant.JAVA);
        generator.gen(Warlock0CodegenConstant.JDBC, Warlock0CodegenConstant.USER, Warlock0CodegenConstant.PASS,
                Warlock3JooqGenerator.includeWarlock());
    }
}

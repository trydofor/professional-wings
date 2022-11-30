package pro.fessional.wings.warlock.project;

import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator.Builder;
import pro.fessional.wings.faceless.project.ProjectEnumGenerator;

import java.util.function.Consumer;

/**
 * idea中，main函数执行和spring执行，workdir不同
 *
 * @author trydofor
 * @since 2021-02-20
 */
public class Warlock2EnumGenerator extends ProjectEnumGenerator {

    public Warlock2EnumGenerator() {
        targetDir = "./wings-warlock/src/main/java/";
        targetPkg = "pro.fessional.wings.warlock.enums.autogen";
    }

    public static Consumer<Builder> excludeWarlock() {
        return builder -> builder
                .excludeType("grant_type")
                .excludeType("user_gender")
                .excludeType("user_status");
    }
}

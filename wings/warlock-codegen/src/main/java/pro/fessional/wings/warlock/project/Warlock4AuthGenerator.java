package pro.fessional.wings.warlock.project;

import pro.fessional.wings.faceless.project.ProjectAuthGenerator;

/**
 * idea中，main函数执行和spring执行，workdir不同
 *
 * @author trydofor
 * @since 2021-02-20
 */
public class Warlock4AuthGenerator extends ProjectAuthGenerator {
    public Warlock4AuthGenerator() {
        targetDir = "./wings-warlock/src/main/java/";
        targetPkg = "pro.fessional.wings.warlock.security.autogen";
    }
}

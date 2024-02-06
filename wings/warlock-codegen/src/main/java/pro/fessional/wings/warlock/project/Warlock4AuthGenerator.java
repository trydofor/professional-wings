package pro.fessional.wings.warlock.project;

import pro.fessional.wings.faceless.project.ProjectAuthGenerator;

/**
 * In IDEA, run from `main` and spring test, they are different in workdir
 *
 * @author trydofor
 * @since 2021-02-20
 */
public class Warlock4AuthGenerator extends ProjectAuthGenerator {
    public Warlock4AuthGenerator() {
        targetDir = "../warlock/src/main/java-gen/";
        targetPkg = "pro.fessional.wings.warlock.security.autogen";
    }
}

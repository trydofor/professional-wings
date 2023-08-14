package pro.fessional.wings.warlock.project;

import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator.Builder;
import pro.fessional.wings.faceless.project.ProjectJooqGenerator;
import pro.fessional.wings.warlock.enums.autogen.GrantType;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;

import java.util.function.Consumer;

/**
 * In IDEA, run from `main` and spring test, they are different in workdir
 *
 * @author trydofor
 * @since 2021-02-20
 */
public class Warlock3JooqGenerator extends ProjectJooqGenerator {

    public Warlock3JooqGenerator() {
        targetDir = "../warlock-autogen/src/main/java/";
        targetPkg = "pro.fessional.wings.warlock.database.autogen";
        configXml = "";
    }

    ///
    public static Consumer<Builder> includeWarlockBase(boolean append) {
        return builder -> builder
                // support Pattern comment
                .databaseIncludes(append, warlockBase);
    }

    public static final String[] warlockBase = {
            "sys_constant_enum",
            "sys_standard_i18n",
            "win_conf_runtime"
    };

    public static Consumer<Builder> includeWarlockBond(boolean append) {
        return builder -> builder
                // support Pattern comment
                .databaseIncludes(append, warlockBond)
                .forcedIntConsEnum(UserGender.class, ".*\\.gender")
                .forcedIntConsEnum(UserStatus.class, "win_user_basis\\.status")
                .forcedIntConsEnum(GrantType.class, "win_.*_grant\\.grant_type")
                ;
    }

    public static final String[] warlockBond = {
            "win_perm_entry",
            "win_role_entry",
            "win_role_grant",
            "win_user_authn",
            "win_user_basis",
            "win_user_grant",
            "win_user_login"
    };

}

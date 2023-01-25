package pro.fessional.wings.faceless.flywave;

import org.jetbrains.annotations.NotNull;

/**
 * pro.fessional.wings/observe/docs/docs/2-faceless/2a-flywave.md
 *
 * @author trydofor
 * @since 2021-03-17
 */
public enum WingsRevision implements RevisionRegister {

    V00_19_0512_01_Schema(2019_0512_01L, "version and journal", "master/00-init"),
    V00_19_0512_02_Fix227(2019_0512_02L, "fix v227", "branch/somefix/01-v227-fix"),
    V01_19_0520_01_IdLog(2019_0520_01L, "lightId and journal", "master/01-light"),
    V01_19_0521_01_EnumI18n(2019_0521_01L, "enum and i18n", "branch/feature/01-enum-i18n"),
    V03_20_1023_01_AuthEnum(2020_1023_01L, "auth enum", "master/03-enum"),
    V04_20_1024_01_UserLogin(2020_1024_01L, "user auth login", "master/04-auth"),
    V04_20_1024_02_RolePermit(2020_1024_02L, "user role permit", "master/04-auth"),
    V05_20_1025_01_ConfRuntime(2020_1025_01L, "runtime config", "master/05-conf"),
    V06_20_1026_01_TinyTask(2020_1026_01L, "tiny task", "master/06-task"),
    V07_20_1027_01_TinyMail(2020_1027_01L, "tiny mail", "master/07-mail"),

    V01_21_0918_01_FixAuthn(2021_0918_01L, "fix authn", "branch/somefix/01-authn-fix"),
    V02_21_1220_01_Fix242(2021_1220_01L, "fix v242.201", "branch/somefix/02-v242-201"),
    ;

    private final long revi;
    private final String info;
    private final String path;

    WingsRevision(long revi, String info, String path) {
        this.revi = revi;
        this.info = info;
        this.path = path;
    }

    @Override
    public long revision() {
        return revi;
    }

    @Override
    public @NotNull String description() {
        return info;
    }

    @Override
    public @NotNull String flywave() {
        return path;
    }
}

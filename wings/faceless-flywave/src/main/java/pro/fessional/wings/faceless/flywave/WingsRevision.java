package pro.fessional.wings.faceless.flywave;

import org.jetbrains.annotations.NotNull;

/**
 * pro.fessional.wings/observe/docs/docs/2-faceless/2a-flywave.md
 *
 * @author trydofor
 * @since 2021-03-17
 */
public enum WingsRevision implements RevisionRegister {
    V00_19_0512_01_Schema(2019_0512_01L, "version and journal", "master/00-init", "wings/faceless-flywave/src/main/resources/wings-flywave"),
    V00_19_0512_02_Fix227(2019_0512_02L, "fix v227", "branch/somefix/01-v227-fix", "wings/faceless-flywave/src/main/resources/wings-flywave"),
    V01_19_0520_01_IdLog(2019_0520_01L, "lightId and journal", "master/01-light", "wings/faceless/src/main/resources/wings-flywave"),
    V01_19_0521_01_EnumI18n(2019_0521_01L, "enum and i18n", "branch/feature/01-enum-i18n", "wings/faceless/src/main/resources/wings-flywave"),
    V03_20_1023_01_AuthEnum(2020_1023_01L, "auth enum", "master/03-enum", "wings/warlock/src/main/resources/wings-flywave"),
    V04_20_1024_01_UserLogin(2020_1024_01L, "user auth login", "master/04-auth", "wings/warlock/src/main/resources/wings-flywave"),
    V04_20_1024_02_RolePermit(2020_1024_02L, "user role permit", "master/04-auth", "wings/warlock/src/main/resources/wings-flywave"),
    V05_20_1025_01_ConfRuntime(2020_1025_01L, "runtime config", "master/05-conf", "wings/warlock/src/main/resources/wings-flywave"),
    V06_20_1026_01_TinyTask(2020_1026_01L, "tiny task", "master/06-task", "radiant/tiny-task/src/main/resources/wings-flywave"),
    V07_20_1027_01_TinyMail(2020_1027_01L, "tiny mail", "master/07-mail", "radiant/tiny-mail/src/main/resources/wings-flywave"),
    V01_21_0918_01_FixAuthn(2021_0918_01L, "fix authn", "branch/somefix/01-authn-fix", "wings/warlock/src/main/resources/wings-flywave"),
    V02_21_1220_01_Fix242(2021_1220_01L, "fix v242.201", "branch/somefix/02-v242-201", "wings/faceless-flywave/src/main/resources/wings-flywave"),

    V90_22_0601_01_TestSchema(2022_0601_01L, "test v1 schema", "master", "wings/testing-faceless/src/main/resources/wings-flywave/"),
    V90_22_0601_02_TestRecord(2022_0601_02L, "test v2 record", "master", "wings/testing-faceless/src/main/resources/wings-flywave/"),
    V91_22_0222_01_ExampleInit(2022_0222_01L, "example demo", "master/00-init", "example/winx-common/src/main/resources/wings-flywave/"),
    ;

    private final long revi;
    private final String info;
    private final String path;
    private final String root;

    WingsRevision(long revi, String info, String path, String root) {
        this.revi = revi;
        this.info = info;
        this.path = path;
        this.root = root;
    }

    @Override
    public long revision() {
        return revi;
    }

    @Override
    @NotNull
    public String description() {
        return info;
    }

    @Override
    @NotNull
    public String flywave() {
        return path;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public String getRoot() {
        return root;
    }
}

package pro.fessional.wings.faceless.flywave;

import org.jetbrains.annotations.NotNull;

/**
 * @author trydofor
 * @since 2021-03-17
 */
public enum WingsRevision implements FlywaveRevisionRegister {

    V00_19_0512_01_Schema(2019_0512_01L, "version and journal", "master/00-init"),
    V00_19_0512_02_Fix227(2019_0512_02L, "fix v227", "branch/somefix/01-v227-fix"),
    V01_19_0520_01_IdLog(2019_0520_01L, "lightId and journal", "master/01-light"),
    V01_19_0521_01_EnumI18n(2019_0521_01L, "enum and i18n", "branch/feature/01-enum-i18n"),
    V04_20_1024_01_UserLogin(2020_1024_01L, "user auth login", "master/04-auth"),
    V04_20_1024_02_RolePermit(2020_1024_02L, "user role permit", "master/04-auth"),
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

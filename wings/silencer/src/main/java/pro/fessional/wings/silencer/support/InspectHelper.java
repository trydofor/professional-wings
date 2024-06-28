package pro.fessional.wings.silencer.support;

import org.springframework.boot.info.GitProperties;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author trydofor
 * @since 2024-06-03
 */
public class InspectHelper {

    public static String jvmName() {
        return System.getProperty("java.vm.name");
    }

    public static String jvmVersion() {
        return System.getProperty("java.vm.version");
    }

    public static String jvmVendor() {
        return System.getProperty("java.vm.vendor");
    }

    ////
    public static String branch(GitProperties git) {
        return git == null ? null : git.getBranch();
    }

    public static String commitIdShort(GitProperties git) {
        return git == null ? null : git.getShortCommitId();
    }

    public static String commitId(GitProperties git) {
        if (git == null) return null;
        String id = git.getCommitId();
        return id != null ? id : git.get("commit.id.full");
    }

    public static String commitDateTime(GitProperties git) {
        return git == null ? null : toDatetime(git.getCommitTime());
    }
    public static String commitDate(GitProperties git) {
        return git == null ? null : toDate(git.getCommitTime());
    }

    public static String commitMessage(GitProperties git) {
        return git == null ? null : git.get("commit.message.full");
    }

    public static String buildDateTime(GitProperties git) {
        return git == null ? null : toDatetime(git.getInstant("build.time"));
    }
    public static String buildDate(GitProperties git) {
        return git == null ? null : toDate(git.getInstant("build.time"));
    }

    public static String buildVersion(GitProperties git) {
        return git == null ? null : git.get("build.version");
    }

    private static String toDatetime(Instant time) {
        if (time == null) return null;
        ZonedDateTime zdt = ZonedDateTime.ofInstant(time, ZoneId.systemDefault());
        return zdt.toString();
    }

    private static String toDate(Instant time) {
        if (time == null) return null;
        ZonedDateTime zdt = ZonedDateTime.ofInstant(time, ZoneId.systemDefault());
        return zdt.toLocalDate().toString();
    }
}

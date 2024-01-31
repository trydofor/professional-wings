package pro.fessional.wings.tiny.mail;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.mail.MailProperties;

/**
 * @author trydofor
 * @since 2024-01-30
 */
public class TestingMailUtil {

    public static final String DRYRUN = "[DRYRUN]";

    public static String dryrun(String sub, MailProperties prop) {
        return dryrun(sub, isDryrun(prop));
    }

    public static String dryrun(String sub, boolean isDryrun) {
        return isDryrun ? dryrun(sub) : sub;
    }

    public static String dryrun(String sub) {
        return DRYRUN + sub;
    }

    public static boolean isDryrun(MailProperties prop) {
        return prop == null
               || StringUtils.isEmpty(prop.getHost())
               || StringUtils.isEmpty(prop.getUsername())
               || StringUtils.isEmpty(prop.getPassword())
                ;
    }
}

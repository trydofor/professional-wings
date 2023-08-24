package pro.fessional.wings.tiny.mail.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring-wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(TinyMailEnabledProp.Key)
public class TinyMailEnabledProp {

    public static final String Key = "spring.wings.tiny.mail.enabled";

    /**
     * whether to enable auto config
     *
     * @see #Key$autoconf
     */
    private boolean autoconf = true;
    public static final String Key$autoconf = Key + ".autoconf";

    /**
     * whether to dry run, log only without actually send
     *
     * @see #Key$dryrun
     */
    private boolean dryrun = false;
    public static final String Key$dryrun = Key + ".dryrun";

    /**
     * whether to enable MailListController
     *
     * @see #Key$controllerList
     */
    private boolean controllerList = true;
    public static final String Key$controllerList = Key + ".controller-list";

    /**
     * whether to enable MailSendController
     *
     * @see #Key$controllerSend
     */
    private boolean controllerSend = true;
    public static final String Key$controllerSend = Key + ".controller-send";

}

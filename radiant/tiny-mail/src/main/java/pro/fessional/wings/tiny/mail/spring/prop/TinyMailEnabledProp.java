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

    public static final String Key = "wings.tiny.mail.enabled";

    /**
     * 是否干跑，仅记录日志不真正执行任务
     *
     * @see #Key$dryrun
     */
    private boolean dryrun = false;
    public static final String Key$dryrun = Key + ".dryrun";

    /**
     * 是否开启 MailListController
     *
     * @see #Key$controllerList
     */
    private boolean controllerList = true;
    public static final String Key$controllerList = Key + ".controller-list";

    /**
     * 是否开启 MailSendController
     *
     * @see #Key$controllerSend
     */
    private boolean controllerSend = true;
    public static final String Key$controllerSend = Key + ".controller-send";

}

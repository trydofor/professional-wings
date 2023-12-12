package pro.fessional.wings.tiny.mail.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.silencer.spring.boot.WingsEnabledCondition;

/**
 * wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(TinyMailEnabledProp.Key)
public class TinyMailEnabledProp {

    public static final String Key = WingsEnabledCondition.Prefix + ".tiny.mail";


    /**
     * whether to enable MailListController
     *
     * @see #Key$mvcList
     */
    private boolean mvcList = true;
    public static final String Key$mvcList = Key + ".mvc-list";

    /**
     * whether to enable MailSendController
     *
     * @see #Key$mvcSend
     */
    private boolean mvcSend = true;
    public static final String Key$mvcSend = Key + ".mvc-send";
}

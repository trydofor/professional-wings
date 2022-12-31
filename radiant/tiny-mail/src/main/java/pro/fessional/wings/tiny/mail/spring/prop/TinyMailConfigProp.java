package pro.fessional.wings.tiny.mail.spring.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.tiny.mail.notice.MailNotice;

import java.util.LinkedHashMap;

/**
 * @author trydofor
 * @since 2022-12-29
 */
@ConfigurationProperties(TinyMailConfigProp.Key)
public class TinyMailConfigProp extends LinkedHashMap<String, MailNotice.Conf> implements InitializingBean {
    public static final String Key = "wings.tiny.mail.config";

    public static final String MailFrom = "spring.mail.mail-from";
    public static final String MailTo = "spring.mail.mail-to";
    public static final String MailCc = "spring.mail.mail-cc";
    public static final String MailBcc = "spring.mail.mail-bcc";
    public static final String MailReply = "spring.mail.mail-reply";
    public static final String MailHtml = "spring.mail.mail-html";


    @Setter(onMethod_ = {@Value("${" + MailFrom + "}")})
    private String from;

    @Setter(onMethod_ = {@Value("${" + MailTo + "}")})
    private String[] to;

    @Setter(onMethod_ = {@Value("${" + MailCc + "}")})
    private String[] cc;

    @Setter(onMethod_ = {@Value("${" + MailBcc + "}")})
    private String[] bcc;

    @Setter(onMethod_ = {@Value("${" + MailReply + "}")})
    private String reply;

    @Setter(onMethod_ = {@Value("${" + MailHtml + "}")})
    private Boolean html;

    @Setter(onMethod_ = {@Autowired})
    private MailProperties props;

    /**
     * 默认属性
     */
    @Getter
    private MailNotice.Conf Default;


    @Override
    public void afterPropertiesSet() {
        Default = get("default");
        if (Default != null) {
            throw new IllegalStateException("must Not have 'default', use spring.mail as default");
        }

        Default = new MailNotice.Conf();
        Default.copy(props);
        Default.setMailFrom(from);
        Default.setMailTo(to);
        Default.setMailCc(cc);
        Default.setMailBcc(bcc);
        Default.setMailReply(reply);
        Default.setMailHtml(html);

        put("default", Default);
    }
}

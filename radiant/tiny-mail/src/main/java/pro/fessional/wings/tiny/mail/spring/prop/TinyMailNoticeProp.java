package pro.fessional.wings.tiny.mail.spring.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.tiny.mail.notice.MailNotice;

import java.util.LinkedHashMap;

/**
 * @author trydofor
 * @since 2022-12-29
 */
@ConfigurationProperties(TinyMailNoticeProp.Key)
public class TinyMailNoticeProp extends LinkedHashMap<String, MailNotice.Conf> implements InitializingBean {
    public static final String Key = "wings.tiny.mail.notice";

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
        if (Default == null) {
            throw new IllegalStateException("must have 'default', use spring.mail as default");
        }
        Default.copy(props);
    }
}

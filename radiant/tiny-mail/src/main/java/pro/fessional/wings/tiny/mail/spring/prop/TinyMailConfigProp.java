package pro.fessional.wings.tiny.mail.spring.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.tiny.mail.sender.TinyMailConfig;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-12-29
 */
@ConfigurationProperties(TinyMailConfigProp.Key)
public class TinyMailConfigProp extends LinkedHashMap<String, TinyMailConfig> implements InitializingBean {
    public static final String Key = "wings.tiny.mail.config";

    public static final String KeyDefault = "default";

    @Setter(onMethod_ = {@Autowired})
    private MailProperties springMail;

    /**
     * 默认属性
     */
    @Getter
    private TinyMailConfig Default;

    @Override
    public void afterPropertiesSet() {
        Default = get(KeyDefault);
        if (Default == null) {
            throw new IllegalStateException("must have 'default', use spring.mail as default");
        }

        Default.setName(KeyDefault);
        Default.setHost(springMail.getHost());
        Default.setPort(springMail.getPort());
        Default.setUsername(springMail.getUsername());
        Default.setPassword(springMail.getPassword());
        Default.setProtocol(springMail.getProtocol());
        Default.setJndiName(springMail.getJndiName());
        Default.setDefaultEncoding(springMail.getDefaultEncoding());
        Default.getProperties().putAll(springMail.getProperties());

        for (Map.Entry<String, TinyMailConfig> en : entrySet()) {
            en.getValue().setName(en.getKey());
        }
    }
}

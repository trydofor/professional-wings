package pro.fessional.wings.tiny.mail.spring.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @since 2022-12-29
 */
@ConfigurationProperties(TinyMailSenderProp.Key)
public class TinyMailSenderProp {
    public static final String Key = "wings.tiny.mail.sender";
}

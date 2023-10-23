package pro.fessional.wings.tiny.mail.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import pro.fessional.wings.tiny.mail.spring.bean.TinyMailConfiguration;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(after = MailSenderAutoConfiguration.class)
@ConditionalOnProperty(name = TinyMailEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration(TinyMailConfiguration.class)
public class TinyMailAutoConfiguration {
}

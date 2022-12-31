package pro.fessional.wings.tiny.mail.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.wings.tiny.mail.notice.MailNotice;
import pro.fessional.wings.tiny.mail.sender.MailSenderProvider;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailConfigProp;

/**
 * @author trydofor
 * @since 2022-08-03
 */

@Configuration(proxyBeanMethods = false)
@ComponentScan({"pro.fessional.wings.tiny.mail.database",
                "pro.fessional.wings.tiny.mail.service"})
@AutoConfigureAfter(MailSenderAutoConfiguration.class)
@RequiredArgsConstructor
public class TinyMailConfiguration {

    private static final Log log = LogFactory.getLog(TinyMailConfiguration.class);

    private final TinyMailConfigProp tinyMailConfigProp;

    @Bean
    public MailSenderProvider mailSenderProvider(JavaMailSender javaMailSender) {
        return new MailSenderProvider(javaMailSender, tinyMailConfigProp);
    }

    @Bean
    public MailNotice mailNotice(MailSenderProvider sender) {
        log.info("TinyMail spring-bean mailNotice");
        return new MailNotice(tinyMailConfigProp, sender);
    }

    @Configuration(proxyBeanMethods = false)
    @ComponentScan("pro.fessional.wings.tiny.mail.controller")
    @ConditionalOnClass(RestController.class)
    public static class MvcController {
    }
}

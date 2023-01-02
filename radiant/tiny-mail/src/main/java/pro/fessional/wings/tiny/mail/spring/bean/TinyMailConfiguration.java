package pro.fessional.wings.tiny.mail.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.wings.tiny.mail.notice.MailNotice;
import pro.fessional.wings.tiny.mail.provider.MailConfigProvider;
import pro.fessional.wings.tiny.mail.provider.MailSenderProvider;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailNoticeProp;

import static org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME;

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

    private final TinyMailNoticeProp tinyMailConfigProp;

    @Bean
    public MailConfigProvider mailConfigProvider() {
        log.info("TinyMail spring-bean mailConfigProvider");
        return new MailConfigProvider(tinyMailConfigProp);
    }

    @Bean
    public MailSenderProvider mailSenderProvider(JavaMailSender javaMailSender, MailProperties mailProperties) {
        log.info("TinyMail spring-bean mailSenderProvider");
        return new MailSenderProvider(javaMailSender, mailProperties);
    }

    @Bean
    public MailNotice mailNotice(MailSenderProvider sender, @Qualifier(DEFAULT_TASK_SCHEDULER_BEAN_NAME) ThreadPoolTaskScheduler executor) {
        log.info("TinyMail spring-bean mailNotice");
        return new MailNotice(tinyMailConfigProp, sender, executor);
    }

    @Configuration(proxyBeanMethods = false)
    @ComponentScan("pro.fessional.wings.tiny.mail.controller")
    @ConditionalOnClass(RestController.class)
    public static class MvcController {
    }
}

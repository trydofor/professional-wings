package pro.fessional.wings.tiny.app.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.tiny.app.service.TestMailSenderManager;
import pro.fessional.wings.tiny.mail.sender.MailSenderManager;
import pro.fessional.wings.tiny.mail.sender.MailSenderProvider;
import pro.fessional.wings.tiny.mail.service.TinyMailService;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailSenderProp;

/**
 * @author trydofor
 * @since 2023-03-07
 */
@Configuration(proxyBeanMethods = false)
@Slf4j
public class TestStatusHookConfiguration {

    @Bean
    public TinyMailService.StatusHook gmailStatusHook() {
        return (po, cost, exception) -> {
            log.info("hook mail subj=" + po.getMailSubj() + ", cost=" + cost, exception);
            return false;
        };
    }

    @Bean
    public MailSenderManager mailSenderManager(TinyMailSenderProp senderProp, MailSenderProvider senderProvider) {
        log.info("TinyMail spring-bean mailSenderManager");
        return new TestMailSenderManager(senderProp, senderProvider);
    }
}

package pro.fessional.wings.tiny.app.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.notice.DingTalkNotice;
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
    public TinyMailService.StatusHook gmailStatusHook(@Autowired(required = false) DingTalkNotice notice) {
        return (po, cost, exception) -> {
            if (notice != null) {
                notice.send("hook mail subj=" + po.getMailSubj() + ", cost=" + cost, po.getMailText());
            }
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

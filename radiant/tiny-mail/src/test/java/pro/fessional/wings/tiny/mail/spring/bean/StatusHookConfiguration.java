package pro.fessional.wings.tiny.mail.spring.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.notice.DingTalkNotice;
import pro.fessional.wings.tiny.mail.service.TinyMailService;

/**
 * @author trydofor
 * @since 2023-03-07
 */
@Configuration
@Slf4j
public class StatusHookConfiguration {

    @Bean
    public TinyMailService.StatusHook gmailStatusHook(@Autowired(required = false) DingTalkNotice notice) {
        return (po, cost, exception) -> {
            if (notice != null) {
                notice.send("hook mail subj=" + po.getMailSubj() + ", cost=" + cost, po.getMailText());
            }
            log.info("hook mail subj=" + po.getMailSubj() + ", cost" + cost, exception);
            return exception != null;
        };
    }
}

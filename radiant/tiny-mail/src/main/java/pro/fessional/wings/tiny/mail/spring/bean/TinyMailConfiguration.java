package pro.fessional.wings.tiny.mail.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.tiny.mail.controller.MailListController;
import pro.fessional.wings.tiny.mail.database.TinyMailDatabase;
import pro.fessional.wings.tiny.mail.sender.MailConfigProvider;
import pro.fessional.wings.tiny.mail.sender.MailNotice;
import pro.fessional.wings.tiny.mail.sender.MailSenderManager;
import pro.fessional.wings.tiny.mail.sender.MailSenderProvider;
import pro.fessional.wings.tiny.mail.service.TinyMailService;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailConfigProp;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailSenderProp;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailServiceProp;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailUrlmapProp;

/**
 * @author trydofor
 * @since 2022-08-03
 */

@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
@EnableConfigurationProperties({
        TinyMailConfigProp.class,
        TinyMailSenderProp.class,
        TinyMailServiceProp.class,
        TinyMailUrlmapProp.class,
})
@RequiredArgsConstructor
public class TinyMailConfiguration {

    private static final Log log = LogFactory.getLog(TinyMailConfiguration.class);


    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    @ComponentScan(basePackageClasses = {TinyMailDatabase.class, TinyMailService.class})
    public static class DaoServScan {
        public DaoServScan() {
            log.info("TinyMail spring-scan database, service");
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    @ComponentScan(basePackageClasses = MailListController.class)
    @ConditionalOnClass(RestController.class)
    public static class MvcRestScan {
        public MvcRestScan() {
            log.info("TinyMail spring-scan controller");
        }
    }

    @Bean
    @ConditionalWingsEnabled
    public MailSenderProvider mailSenderProvider(JavaMailSender defaultSender) {
        log.info("TinyMail spring-bean mailSenderProvider");
        return new MailSenderProvider(defaultSender);
    }

    @Bean
    @ConditionalWingsEnabled
    public MailConfigProvider mailConfigProvider(TinyMailConfigProp tinyMailConfigProp) {
        log.info("TinyMail spring-bean mailConfigProvider");
        return new MailConfigProvider(tinyMailConfigProp);
    }

    @Bean
    @ConditionalWingsEnabled
    public MailSenderManager mailSenderManager(TinyMailSenderProp senderProp, MailSenderProvider senderProvider) {
        log.info("TinyMail spring-bean mailSenderManager");
        return new MailSenderManager(senderProp, senderProvider);
    }

    @Bean
    @ConditionalWingsEnabled
    public MailNotice mailNotice(MailConfigProvider configProvider, MailSenderManager senderManager) {
        log.info("TinyMail spring-bean mailNotice");
        return new MailNotice(configProvider, senderManager);
    }
}

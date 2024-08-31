package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.mirana.tk.TicketHelp;
import pro.fessional.wings.silencer.encrypt.SecretProvider;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService;
import pro.fessional.wings.warlock.service.auth.impl.SimpleTicketServiceImpl;
import pro.fessional.wings.warlock.service.auth.impl.WarlockOauthServiceImpl;
import pro.fessional.wings.warlock.spring.prop.WarlockTicketProp;

import java.util.Map;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class WarlockOauthTicketConfiguration {

    private final static Log log = LogFactory.getLog(WarlockOauthTicketConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public WarlockOauthServiceImpl warlockOauthService(WarlockTicketProp warlockTicketProp) {
        log.info("WarlockShadow spring-bean warlockOauthService");
        WarlockOauthServiceImpl bean = new WarlockOauthServiceImpl();
        bean.setAuthCodeTtl(warlockTicketProp.getCodeTtl());
        bean.setAccessTokenTtl(warlockTicketProp.getTokenTtl());

        return bean;
    }

    @Bean
    @ConditionalWingsEnabled
    public SimpleTicketServiceImpl warlockTicketService(WarlockTicketProp warlockTicketProp, SecretProvider secretProvider) {
        log.info("WarlockShadow spring-bean warlockTicketService");
        SimpleTicketServiceImpl bean = new SimpleTicketServiceImpl();
        bean.setAuthorizeCodeMax(warlockTicketProp.getCodeMax());
        bean.setAccessTokenMax(warlockTicketProp.getTokenMax());

        String key = secretProvider.tryGet(SecretProvider.Ticket);
        if (key == null || key.isBlank()) {
            log.info("WarlockShadow spring-conf random aes-key, may fail api-call in cluster");
            key = RandCode.strong(32);
        }
        bean.setHelper(new TicketHelp.Ah1Help(warlockTicketProp.getPubMod(), key));

        for (Map.Entry<String, WarlockTicketService.Pass> en : warlockTicketProp.getClient().entrySet()) {
            final WarlockTicketService.Pass pass = en.getValue();
            String client = pass.getClient();
            if (client == null || client.isBlank()) {
                client = en.getKey();
                pass.setClient(client);
            }

            final String secret = pass.getSecret();
            if (secret == null || secret.isEmpty()) {
                log.warn("WarlockShadow spring-conf skip warlockTicketService.client=" + client + " for empty secret");
                continue;
            }
            log.info("WarlockShadow spring-conf warlockTicketService.client=" + client);
            bean.addClient(pass);
        }
        return bean;
    }
}

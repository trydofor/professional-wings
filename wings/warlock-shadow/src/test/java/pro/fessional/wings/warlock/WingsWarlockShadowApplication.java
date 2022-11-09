package pro.fessional.wings.warlock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService;
import pro.fessional.wings.warlock.service.auth.impl.SimpleTicketServiceImpl;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
public class WingsWarlockShadowApplication {

    @Bean
    public WarlockTicketService warlockTicketService() {
        SimpleTicketServiceImpl bean = new SimpleTicketServiceImpl();
        final WarlockTicketService.Pass pass = new WarlockTicketService.Pass();
        pass.setUserId(79L);
        pass.setClient("wings-trydofor");
        pass.setSecret("wings-trydofor");
        bean.addClient(pass);
        return bean;
    }

    public static void main(String[] args) {
        SpringApplication.run(WingsWarlockShadowApplication.class, args);
    }
}

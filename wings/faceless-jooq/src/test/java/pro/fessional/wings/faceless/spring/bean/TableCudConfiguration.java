package pro.fessional.wings.faceless.spring.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.service.WingsTableCudHandlerTest;

/**
 * @author trydofor
 * @since 2019-08-12
 */
@Configuration(proxyBeanMethods = false)
public class TableCudConfiguration {

    @Bean
    public WingsTableCudHandlerTest wingsTableCudHandler() {
        return new WingsTableCudHandlerTest();
    }

}

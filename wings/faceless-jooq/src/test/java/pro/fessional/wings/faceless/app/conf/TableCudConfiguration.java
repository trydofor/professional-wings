package pro.fessional.wings.faceless.app.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.app.service.TestingTableCudHandler;

/**
 * @author trydofor
 * @since 2019-08-12
 */
@Configuration(proxyBeanMethods = false)
public class TableCudConfiguration {

    @Bean
    public TestingTableCudHandler wingsTableCudHandler() {
        return new TestingTableCudHandler();
    }

}

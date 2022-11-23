package pro.fessional.wings.faceless.spring.bean;

import org.jooq.ExecuteListenerProvider;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.jooq.listener.SlowSqlListener;
import pro.fessional.wings.faceless.jooq.JooqTableCudListenerTest;
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

    @Bean
    public ExecuteListenerProvider jooqSlowSqlListener() {
        SlowSqlListener listener = new SlowSqlListener();
        listener.setThresholdMillis(0);
        listener.setCostAndSqlConsumer(JooqTableCudListenerTest.SlowSql);
        return new DefaultExecuteListenerProvider(listener);
    }
}

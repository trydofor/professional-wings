package pro.fessional.wings.faceless.spring.bean;

import org.jooq.ExecuteListenerProvider;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.jooq.listener.SlowSqlListener;
import pro.fessional.wings.faceless.jooq.JooqTableCudListenerTest;

/**
 * @author trydofor
 * @since 2019-08-12
 */
@Configuration(proxyBeanMethods = false)
public class SlowSqlConfiguration {

    @Bean
    public ExecuteListenerProvider jooqSlowSqlListener() {
        SlowSqlListener listener = new SlowSqlListener();
        listener.setThresholdMillis(0);
        listener.setToken("SlowSqlListener-TEST-SHOW");
        listener.setCostAndSqlConsumer(JooqTableCudListenerTest.SlowSql);
        return new DefaultExecuteListenerProvider(listener);
    }
}

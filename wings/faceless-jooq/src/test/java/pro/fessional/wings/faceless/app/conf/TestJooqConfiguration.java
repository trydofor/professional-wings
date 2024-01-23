package pro.fessional.wings.faceless.app.conf;

import org.jooq.ConnectionProvider;
import org.jooq.ExecuteListenerProvider;
import org.jooq.impl.DefaultConnectionProvider;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockConnectionProvider;
import org.jooq.tools.jdbc.MockDataProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.app.database.jooq.TestNormalTableDataProvider;
import pro.fessional.wings.faceless.app.service.TestTableCudHandler;
import pro.fessional.wings.faceless.database.jooq.listener.SlowSqlListener;
import pro.fessional.wings.faceless.jooq.JooqTableCudListenerTest;

/**
 * @author trydofor
 * @since 2023-11-10
 */
@Configuration(proxyBeanMethods = false)
public class TestJooqConfiguration {

    @Bean
    @ConditionalOnProperty(name = "wings.testing.faceless.mock-jooq", havingValue = "true")
    public ConnectionProvider mockConnectionProvider() {
        MockDataProvider provider = new TestNormalTableDataProvider();
        MockConnection connection = new MockConnection(provider);
        DefaultConnectionProvider delegate = new DefaultConnectionProvider(connection);
        return new MockConnectionProvider(delegate, provider);//
    }

    @Bean
    public ExecuteListenerProvider jooqSlowSqlListener() {
        SlowSqlListener listener = new SlowSqlListener();
        listener.setThresholdMillis(0);
        listener.setToken("SlowSqlListener-TEST-SHOW");
        listener.setCostAndSqlConsumer(JooqTableCudListenerTest.SlowSql);
        return new DefaultExecuteListenerProvider(listener);
    }

    @Bean
    public TestTableCudHandler wingsTableCudHandler() {
        return new TestTableCudHandler();
    }
}

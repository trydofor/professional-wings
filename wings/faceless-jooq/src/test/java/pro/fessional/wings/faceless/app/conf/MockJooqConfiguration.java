package pro.fessional.wings.faceless.app.conf;

import org.jooq.ConnectionProvider;
import org.jooq.impl.DefaultConnectionProvider;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockConnectionProvider;
import org.jooq.tools.jdbc.MockDataProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.app.database.jooq.MockTstNormalTableDataProvider;

/**
 * @author trydofor
 * @since 2023-11-10
 */
@Configuration(proxyBeanMethods = false)
public class MockJooqConfiguration {

    @Bean
    @ConditionalOnProperty(name = "wings.faceless.testing.mock-jooq", havingValue = "true")
    public ConnectionProvider mockConnectionProvider() {
        MockDataProvider provider = new MockTstNormalTableDataProvider();
        MockConnection connection = new MockConnection(provider);
        DefaultConnectionProvider delegate = new DefaultConnectionProvider(connection);
        return new MockConnectionProvider(delegate, provider);//
    }
}

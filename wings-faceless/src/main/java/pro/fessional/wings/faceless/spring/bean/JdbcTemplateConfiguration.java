package pro.fessional.wings.faceless.spring.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author trydofor
 * @since 2019-06-25
 */
@Configuration
@ConditionalOnProperty(name = "spring.wings.faceless.jdbctemplate.enabled", havingValue = "true")
public class JdbcTemplateConfiguration {

    @Bean
    @ConditionalOnMissingBean(JdbcTemplate.class)
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

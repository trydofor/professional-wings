package pro.fessional.wings.faceless.spring.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;

import javax.sql.DataSource;

/**
 * @author trydofor
 * @since 2019-06-25
 */
@Configuration
@ConditionalOnProperty(name = FacelessEnabledProp.Key$jdbctemplate, havingValue = "true")
public class FacelessJdbcConfiguration {

    @Bean
    @ConditionalOnMissingBean(JdbcTemplate.class)
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

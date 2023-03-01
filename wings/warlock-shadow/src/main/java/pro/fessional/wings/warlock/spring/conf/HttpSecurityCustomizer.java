package pro.fessional.wings.warlock.spring.conf;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @author trydofor
 * @since 2023-01-20
 */
public interface HttpSecurityCustomizer {
    void customize(HttpSecurity http) throws Exception;
}

package pro.fessional.wings.slardar.spring.help;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import pro.fessional.wings.slardar.security.WingsUserDetailsService;
import pro.fessional.wings.slardar.spring.conf.WingsBindAuthnConfigurer;
import pro.fessional.wings.slardar.spring.conf.WingsBindLoginConfigurer;
import pro.fessional.wings.slardar.spring.conf.WingsHttpPermitConfigurer;

/**
 * @author trydofor
 * @since 2020-08-10
 */
public class SecurityConfigHelper {


    /**
     * <pre>
     * public class Config extends WebSecurityConfigurerAdapter {
     *     protected void configure(HttpSecurity http) throws Exception {
     *         http
     *             .apply(wingsPermit())
     *                 .permitAllCors(true)
     *                 .and()
     *             ...;
     *     }
     * }
     * </pre>
     *
     * @return customer dsl
     */
    public static HttpHelper http() {
        return new HttpHelper();
    }

    public static class HttpHelper extends AbstractHttpConfigurer<HttpHelper, HttpSecurity> {
        public HttpHelper httpPermit(Customizer<WingsHttpPermitConfigurer> customizer) throws Exception {
            final WingsHttpPermitConfigurer conf = getBuilder().apply(new WingsHttpPermitConfigurer());
            customizer.customize(conf);
            return this;
        }

        public HttpHelper bindLogin(Customizer<WingsBindLoginConfigurer> customizer) throws Exception {
            final WingsBindLoginConfigurer conf = getBuilder().apply(new WingsBindLoginConfigurer());
            customizer.customize(conf);
            return this;
        }
    }

    public static WingsBindAuthnConfigurer<WingsUserDetailsService> auth() {
        return new WingsBindAuthnConfigurer<>();
    }
}

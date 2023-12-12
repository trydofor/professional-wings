package pro.fessional.wings.slardar.spring.conf;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.slardar.security.PasssaltEncoder;
import pro.fessional.wings.slardar.security.WingsAuthCheckService;
import pro.fessional.wings.slardar.security.WingsUserDetailsService;
import pro.fessional.wings.slardar.security.bind.WingsBindAuthProvider;

/**
 * order before InitializeUserDetailsBeanManagerConfigurer.DEFAULT_ORDER
 *
 * @author trydofor
 * @since 2021-02-09
 */
@Order(WingsSecBeanInitConfigurer.ORDER)
public class WingsSecBeanInitConfigurer extends GlobalAuthenticationConfigurerAdapter {

    public static final int ORDER = WingsOrdered.Lv1Config;

    private final ApplicationContext context;

    public WingsSecBeanInitConfigurer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.apply(new InitializeUserDetailsManagerConfigurer());
    }

    class InitializeUserDetailsManagerConfigurer extends GlobalAuthenticationConfigurerAdapter {

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            if (auth.isConfigured()) {
                return;
            }
            WingsUserDetailsService userDetailsService = getBeanOrNull(WingsUserDetailsService.class);
            if (userDetailsService == null) {
                return;
            }

            PasssaltEncoder passsaltEncoder = getBeanOrNull(PasssaltEncoder.class);
            PasswordEncoder passwordEncoder = getBeanOrNull(PasswordEncoder.class);
            UserDetailsPasswordService passwordManager = getBeanOrNull(UserDetailsPasswordService.class);
            WingsAuthCheckService wingsAuthCheckService = getBeanOrNull(WingsAuthCheckService.class);

            WingsBindAuthProvider provider = new WingsBindAuthProvider(userDetailsService);

            if (passsaltEncoder != null) {
                provider.setPasssaltEncoder(passsaltEncoder);
            }

            if (passwordEncoder != null) {
                provider.setPasswordEncoder(passwordEncoder);
            }

            if (passwordManager != null) {
                provider.setUserDetailsPasswordService(passwordManager);
            }

            if (wingsAuthCheckService != null) {
                provider.setWingsAuthCheckService(wingsAuthCheckService);
            }

            provider.afterPropertiesSet();
            auth.authenticationProvider(provider);
        }

        /**
         * @return a bean of the requested class if there's just a single registered
         * component, null otherwise.
         */
        private <T> T getBeanOrNull(Class<T> type) {
            String[] beanNames = WingsSecBeanInitConfigurer.this.context.getBeanNamesForType(type);
            if (beanNames.length != 1) {
                return null;
            }
            return WingsSecBeanInitConfigurer.this.context.getBean(beanNames[0], type);
        }
    }
}

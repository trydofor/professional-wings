package pro.fessional.wings.slardar.security.conf;

import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.UserDetailsAwareConfigurer;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import pro.fessional.wings.slardar.security.auth.WingsBindAuthnProvider;

/**
 * @author trydofor
 * @since 2021-02-09
 */
public class WingsBindAuthnConfigurer<U extends UserDetailsService> extends UserDetailsAwareConfigurer<AuthenticationManagerBuilder, U> {

    private U userDetailsService;
    private PasswordEncoder passwordEncoder;
    private UserDetailsPasswordService userDetailsPasswordService;
    private boolean wingsBindAuthnProvider = true;

    public WingsBindAuthnConfigurer<U> userDetailsService(U userDetailsService) {
        this.userDetailsService = userDetailsService;
        return this;
    }

    public WingsBindAuthnConfigurer<U> withObjectPostProcessor(ObjectPostProcessor<?> objectPostProcessor) {
        addObjectPostProcessor(objectPostProcessor);
        return this;
    }

    public WingsBindAuthnConfigurer<U> passwordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        return this;
    }

    public WingsBindAuthnConfigurer<U> userDetailsPasswordManager(UserDetailsPasswordService passwordManager) {
        this.userDetailsPasswordService = passwordManager;
        return this;
    }

    public WingsBindAuthnConfigurer<U> wingsBindAuthnProvider(boolean bool) {
        this.wingsBindAuthnProvider = bool;
        return this;
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) {
        if (!wingsBindAuthnProvider) return;

        WingsBindAuthnProvider provider = new WingsBindAuthnProvider(userDetailsService);

        if (passwordEncoder != null) {
            provider.setPasswordEncoder(passwordEncoder);
        }

        if (userDetailsPasswordService != null) {
            provider.setUserDetailsPasswordService(userDetailsPasswordService);
        }

        provider = postProcess(provider);
        builder.authenticationProvider(provider);
    }

    @Override
    public U getUserDetailsService() {
        return this.userDetailsService;
    }
}

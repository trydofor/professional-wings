package pro.fessional.wings.slardar.spring.conf;

import org.jetbrains.annotations.Contract;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.UserDetailsAwareConfigurer;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import pro.fessional.wings.slardar.security.WingsAuthCheckService;
import pro.fessional.wings.slardar.security.bind.WingsBindAuthProvider;

/**
 * @author trydofor
 * @since 2021-02-09
 */
public class WingsBindAuthnConfigurer<U extends UserDetailsService> extends UserDetailsAwareConfigurer<AuthenticationManagerBuilder, U> {

    private U userDetailsService;
    private PasswordEncoder passwordEncoder;
    private UserDetailsPasswordService userDetailsPasswordService;
    private WingsAuthCheckService wingsAuthCheckService;
    private boolean wingsBindAuthnProvider = true;

    @Contract("_->this")
    public WingsBindAuthnConfigurer<U> userDetailsService(U userDetailsService) {
        this.userDetailsService = userDetailsService;
        return this;
    }

    @Contract("_->this")
    public WingsBindAuthnConfigurer<U> withObjectPostProcessor(ObjectPostProcessor<?> objectPostProcessor) {
        addObjectPostProcessor(objectPostProcessor);
        return this;
    }

    @Contract("_->this")
    public WingsBindAuthnConfigurer<U> passwordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        return this;
    }

    @Contract("_->this")
    public WingsBindAuthnConfigurer<U> userDetailsPasswordManager(UserDetailsPasswordService passwordManager) {
        this.userDetailsPasswordService = passwordManager;
        return this;
    }

    @Contract("_->this")
    public WingsBindAuthnConfigurer<U> wingsBindAuthnProvider(boolean bool) {
        this.wingsBindAuthnProvider = bool;
        return this;
    }

    @Contract("_->this")
    public WingsBindAuthnConfigurer<U> wingsAuthCheckService(WingsAuthCheckService wingsAuthCheckService) {
        this.wingsAuthCheckService = wingsAuthCheckService;
        return this;
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) {
        if (!wingsBindAuthnProvider) return;

        WingsBindAuthProvider provider = new WingsBindAuthProvider(userDetailsService);

        if (passwordEncoder != null) {
            provider.setPasswordEncoder(passwordEncoder);
        }

        if (userDetailsPasswordService != null) {
            provider.setUserDetailsPasswordService(userDetailsPasswordService);
        }

        if (wingsAuthCheckService != null) {
            provider.setWingsAuthCheckService(wingsAuthCheckService);
        }

        provider = postProcess(provider);
        builder.authenticationProvider(provider);
    }

    @Override
    public U getUserDetailsService() {
        return this.userDetailsService;
    }
}

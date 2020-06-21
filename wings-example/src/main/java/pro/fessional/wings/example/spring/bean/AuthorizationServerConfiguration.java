package pro.fessional.wings.example.spring.bean;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import pro.fessional.wings.slardar.spring.bean.WingsOAuth2xConfiguration;

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Setter(onMethod = @__({@Autowired}))
    private WingsOAuth2xConfiguration.Helper helper;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        helper.configure(clients);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        helper.configure(endpoints);

    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        helper.configure(security);
    }
}
package pro.fessional.wings.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarCookieConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarDebounceConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarDomainExtendConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarDoubleKillWebConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarFirstBloodConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarJacksonWebConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarLocaleConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarOkhttpWebConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarOverloadConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarPageQueryConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarRemoteConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarRestreamConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarRighterConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarSessionConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarSwaggerConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarTerminalConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarUndertowConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarWebMvcConfiguration;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(before = WebMvcAutoConfiguration.class)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration({
        SlardarCookieConfiguration.class,
        SlardarDebounceConfiguration.class,
        SlardarDomainExtendConfiguration.class,
        SlardarDoubleKillWebConfiguration.class,
        SlardarFirstBloodConfiguration.class,
        SlardarJacksonWebConfiguration.class,
        SlardarLocaleConfiguration.class,
        SlardarOkhttpWebConfiguration.class,
        SlardarOverloadConfiguration.class,
        SlardarPageQueryConfiguration.class,
        SlardarRemoteConfiguration.class,
        SlardarRestreamConfiguration.class,
        SlardarRighterConfiguration.class,
        SlardarSessionConfiguration.class,
        SlardarSwaggerConfiguration.class,
        SlardarTerminalConfiguration.class,
        SlardarUndertowConfiguration.class,
        SlardarWebMvcConfiguration.class,
})
public class SlardarWebmvcAutoConfiguration {
}

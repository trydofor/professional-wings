package pro.fessional.wings.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.spring.bean.SlardarCookieConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarDebounceConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarDomainExtendConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarDoubleKillWebConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarFirstBloodConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarJacksonWebConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarOverloadConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarPageQueryConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarRemoteConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarReuseStreamConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarRighterConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarSessionConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarSwaggerConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarTerminalConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarUndertowConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarWebMvcConfiguration;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalWingsEnabled
@Import({
        SlardarCookieConfiguration.class,
        SlardarDebounceConfiguration.class,
        SlardarDomainExtendConfiguration.class,
        SlardarDoubleKillWebConfiguration.class,
        SlardarFirstBloodConfiguration.class,
        SlardarJacksonWebConfiguration.class,
        SlardarOverloadConfiguration.class,
        SlardarPageQueryConfiguration.class,
        SlardarRemoteConfiguration.class,
        SlardarReuseStreamConfiguration.class,
        SlardarRighterConfiguration.class,
        SlardarSessionConfiguration.class,
        SlardarSwaggerConfiguration.class,
        SlardarTerminalConfiguration.class,
        SlardarUndertowConfiguration.class,
        SlardarWebMvcConfiguration.class,
})
public class SlardarWebFunAutoConfiguration {
}

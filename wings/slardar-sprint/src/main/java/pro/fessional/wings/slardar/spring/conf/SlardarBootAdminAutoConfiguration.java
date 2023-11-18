package pro.fessional.wings.slardar.spring.conf;

import de.codecentric.boot.admin.client.config.SpringBootAdminClientAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.spring.bean.SlardarBootAdminClientConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarBootAdminServerConfiguration;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(before = SpringBootAdminClientAutoConfiguration.class)
@ConditionalOnClass(SpringBootAdminClientAutoConfiguration.class)
@ConditionalWingsEnabled
@Import({
        SlardarBootAdminClientConfiguration.class,
        SlardarBootAdminServerConfiguration.class
})
public class SlardarBootAdminAutoConfiguration {
}

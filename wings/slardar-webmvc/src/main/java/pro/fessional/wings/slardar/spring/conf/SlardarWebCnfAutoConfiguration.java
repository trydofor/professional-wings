package pro.fessional.wings.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.spring.bean.SlardarLocaleConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarOkhttpWebConfiguration;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(before = WebMvcAutoConfiguration.class)
@ConditionalWingsEnabled
@Import({
        SlardarLocaleConfiguration.class,
        SlardarOkhttpWebConfiguration.class,
})
public class SlardarWebCnfAutoConfiguration {
}

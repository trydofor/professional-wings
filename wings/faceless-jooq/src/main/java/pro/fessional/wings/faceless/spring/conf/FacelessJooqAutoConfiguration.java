package pro.fessional.wings.faceless.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.faceless.spring.bean.FacelessJooqConfiguration;
import pro.fessional.wings.faceless.spring.bean.FacelessJooqCudConfiguration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @since 2019-06-01
 */

@AutoConfiguration
@ConditionalWingsEnabled
@Import({FacelessJooqConfiguration.class, FacelessJooqCudConfiguration.class})
public class FacelessJooqAutoConfiguration {
}

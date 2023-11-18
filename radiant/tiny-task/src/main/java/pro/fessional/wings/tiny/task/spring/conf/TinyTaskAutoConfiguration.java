package pro.fessional.wings.tiny.task.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.tiny.task.spring.bean.TinyTaskConfiguration;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalWingsEnabled
@Import(TinyTaskConfiguration.class)
public class TinyTaskAutoConfiguration {
}

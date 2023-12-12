package pro.fessional.wings.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.spring.bean.SlardarAsyncConfiguration;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(before = TaskExecutionAutoConfiguration.class)
@ConditionalWingsEnabled
@Import(SlardarAsyncConfiguration.class)
public class SlardarAsyncAutoConfiguration {
}

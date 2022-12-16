package pro.fessional.wings.tiny.task.schedule;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 标记任务Bean的执行方法。注意事项如下，
 * 无参数的方法，可被自动注册和执行，
 * 有参数的方法，只能手动注册并执行。
 * 不可使用多态，仅通过方法名定位（简化及可读性），不使用参数列表
 * 若有参数，则其必须是具体类型，无状态的，可被序列化的（默认json）
 * </pre>
 *
 * @author trydofor
 * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
 * @since 2022-12-01
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TinyTasker {

    /**
     * 配置的属性名，默认为Class#method，
     * 其属性类型为Map[String,TaskerConf]，位于前缀默认为wings.tiny.task.define下
     * 如 wings.tiny.task.define[pro.fessional.wings.tiny.task.schedule.TaskerTest#test].enable=false
     */
    String value() default "";

    /**
     * 对SpringBean增加此标记类，可被Wings在启动时自动配置
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Component
    public @interface Auto {
    }
}

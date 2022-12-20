package pro.fessional.wings.tiny.task.schedule;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 除value外，其他属性推荐使用配置文件！
 * 标记任务Bean的执行方法。注意事项如下，
 * 无参数的方法，可被自动注册和执行，
 * 有参数的方法，只能手动注册并执行。
 * 不可使用多态，仅通过方法名定位（简化及可读性），不使用参数列表
 * 若有参数，则其必须是具体类型，无状态的，可被序列化的（默认json）
 *
 * annotation优先级低于property，高于default。
 * 合并后的配置，最终写入database，以id管理task
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
     * propkey，配置的属性名，默认为Class#method，
     * 其属性类型为Map[String,TaskerConf]，位于前缀默认为wings.tiny.task.define下
     * 如 wings.tiny.task.define[pro.fessional.wings.tiny.task.schedule.TaskerTest#test].enable=false
     */
    String value() default "";

    /**
     * timingZone，调度时区的ZoneId格式，默认系统时区，null及空时使用Default配置
     */
    String zone() default "";


    /**
     * timingCron，调度表达式内容，最高优先级，受timingType影响，默认spring cron格式（秒分时日月周），不会使用Default配置
     */
    String cron() default "";

    /**
     * timingIdle，固定空闲相连（秒），优先级次于timingCron，相当于fixedDelay，结束到开始，0为无效，不会使用Default配置
     */
    int idle() default 0;

    /**
     * timingRate，固定频率开始（秒），优先级次于timingIdle，相当于fixedRate，开始到开始，0为无效，不会使用Default配置
     */
    int rate() default 0;


    /**
     * 对SpringBean增加此标记类，可被Wings在启动时自动配置
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Component
    public @interface Auto {
    }
}
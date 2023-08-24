package pro.fessional.wings.tiny.task.schedule;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * A config file is recommended for all properties except `value`!
 *
 * Marks the execution method of the Task Bean. Note the following.
 * - Methods without parameters can be registered and executed automatically.
 * - Methods with parameters can only be registered and executed manually.
 * - Polymorphism is not allowed, methods are located by name only
 *   (for simplicity and readability), and parameter lists are not used.
 * - If there is a parameter, it must be of a specific type, stateless,
 *   and serializable (json by default).
 *
 * Annotation has a lower priority than property and a higher priority than default.
 * The merged config is eventually saved to database, which manages the task with the id
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
     * `propkey`, the key of the property, which defaults to `Class#method`.
     * Its property type is Map[String,TaskerConf], located under the prefix `wings.tiny.task.define`
     * e.g. `wings.tiny.task.define[pro.fessional.wings.tiny.task.schedule.TaskerTest#test].enable=false`
     */
    String value() default "";

    /**
     * timingZone, scheduling timezone in ZoneId format, default system time zone.
     * Use the `Default` config if null or empty.
     */
    String zone() default "";


    /**
     * timingCron, scheduling expression, highest priority, affected by timingType,
     * default spring cron format (seconds minutes hours days months weeks).
     * Not use the `Default` config
     */
    String cron() default "";

    /**
     * timingIdle, fixed idle between (seconds),
     * priority second to timingCron,equivalent to fixedDelay.
     * end to start, 0 is invalid, Not use the `Default` config
     *
     */
    int idle() default 0;

    /**
     * timingRate, fixed frequency start (seconds),
     * priority second to timingIdle, equivalent to fixedRate.
     * start to start, 0 is invalid, Not use the `Default` config
     */
    int rate() default 0;


    /**
     * Adding to a SpringBean can be auto config by Wings at startup.
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Component
    public @interface Auto {
    }
}

package pro.fessional.wings.silencer.watch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基于AOP，对方法进行Watching
 *
 * @author trydofor
 * @see pro.fessional.mirana.time.StopWatch
 * @since 2022-11-21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Watching {
    /**
     * 名字，为空时，AOP自动设置
     */
    String value() default "";

    /**
     * 阈值的毫秒数，-1表示关闭此功能
     */
    long threshold() default -1;
}

package pro.fessional.wings.slardar.concur;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来注解需要验证码支持的RequestMapping
 *
 * @author trydofor
 * @since 2021-03-10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FirstBlood {

    /**
     * 两次请求触发验证的间隔秒数，默认0，表示每次都验证。
     *
     * @return 计数秒数
     */
    int first() default 0;


    /**
     * 验证或禁用的持续秒数。以10秒为一个阶梯，建议不超过1天。
     *
     * @return 掉血秒数
     */
    int blood() default 300;

    /**
     * 试错的次数，超过时重发验证码或禁用
     *
     * @return 试错的次数
     */
    int retry() default 1;


    /**
     * 验证场景，会传递给interceptor
     *
     * @return 验证场景
     */
    String scene() default "";
}

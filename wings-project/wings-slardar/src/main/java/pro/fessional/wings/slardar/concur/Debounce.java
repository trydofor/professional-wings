package pro.fessional.wings.slardar.concur;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 先调用后等待的防抖，默认同一session防抖时间内只能被执行一次。
 *
 * @author trydofor
 * @since 2022-05-29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Debounce {

    /**
     * 是否复用之前的请求，还是直接返回。
     *
     * @return 是否复用
     */
    boolean reuse() default false;

    /**
     * 防抖的等待间隔，毫秒
     *
     * @return 防抖间隔
     */
    long waiting() default 500;

    /**
     * 组合key中是否包含sessionId
     *
     * @return 是否包含
     */
    boolean session() default true;

    /**
     * 组合key中是否包含method
     *
     * @return 是否包含
     */
    boolean method() default true;

    /**
     * 组合key中是否包含querystring
     *
     * @return 是否包含
     */
    boolean query() default true;

    /**
     * 组合key中包含的header name
     *
     * @return header数组
     */
    String[] header() default {};

    /**
     * 组合key中包含的body的md5sum或length。
     * 如果request支wings流复用，则采用md5，否则取length
     *
     * @return 是否包含
     */
    boolean body() default false;
}

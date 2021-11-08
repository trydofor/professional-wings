package pro.fessional.wings.slardar.concur;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author trydofor
 * @since 2021-10-19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Righter {

    /**
     * 提交方法建议true，无header时审查失败。读取方法建议false，以提供审查数据
     *
     * @return 是否强制验证
     */
    boolean value() default true;
}

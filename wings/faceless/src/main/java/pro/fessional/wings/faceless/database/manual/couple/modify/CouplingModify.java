package pro.fessional.wings.faceless.database.manual.couple.modify;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 耦合变更，多表或关联多表，一般为join查询或子查询，包名以主表命名
 *
 * @author trydofor
 * @since 2023-02-03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
public @interface CouplingModify {
}

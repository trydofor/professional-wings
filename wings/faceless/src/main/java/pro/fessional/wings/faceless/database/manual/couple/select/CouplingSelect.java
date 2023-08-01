package pro.fessional.wings.faceless.database.manual.couple.select;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Coupling select, multiple tables or associated multiple tables.
 * Generally join query or sub-query, package should be named by the main table
 *
 * @author trydofor
 * @since 2023-02-03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
public @interface CouplingSelect {
}

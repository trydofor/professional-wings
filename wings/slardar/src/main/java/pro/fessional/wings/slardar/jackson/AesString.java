package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author trydofor
 * @since 2022-12-05
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = AesStringSerializer.class)
@JsonDeserialize(using = AesStringDeserializer.class)
public @interface AesString {
    /**
     * The name of the Aes to use, default `system` Aes.
     */
    @NotNull
    String value() default "";

    /**
     * Misfire policy, default Error
     */
    @NotNull
    Misfire misfire() default Misfire.Error;

    enum Misfire {
        /**
         * throw exception
         */
        Error,
        /**
         * set empty
         */
        Empty,
        /**
         * set ValueMask ('*****')
         */
        Masks,
    }
}

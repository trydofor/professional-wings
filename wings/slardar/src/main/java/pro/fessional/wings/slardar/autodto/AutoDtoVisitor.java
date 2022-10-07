package pro.fessional.wings.slardar.autodto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.anti.BeanVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author trydofor
 * @since 2022-10-05
 */
public class AutoDtoVisitor extends BeanVisitor.ContainerVisitor {


    @Override
    public boolean cares(@NotNull Field field, @NotNull Annotation[] annos) {
        for (Annotation an : annos) {
            if (AutoDtoAble.class.equals(an.annotationType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nullable
    protected Object amendValue(@NotNull Field field, @NotNull Annotation[] annos, @Nullable Object obj) {
        return obj;
    }
}

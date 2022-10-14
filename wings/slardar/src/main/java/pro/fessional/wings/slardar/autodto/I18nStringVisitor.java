package pro.fessional.wings.slardar.autodto;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSource;
import pro.fessional.mirana.anti.BeanVisitor;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.i18n.I18nString;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * @author trydofor
 * @since 2022-10-05
 */
@RequiredArgsConstructor
public class I18nStringVisitor extends BeanVisitor.ContainerVisitor {

    private final MessageSource messageSource;
    private final Supplier<Locale> localeSupplier;

    @Override
    public boolean cares(@NotNull Field field, @NotNull Annotation[] annos) {
        for (Annotation an : annos) {
            if (AutoI18nString.class.equals(an.annotationType())) {
                return ((AutoI18nString) an).value();
            }
        }
        return false;
    }

    @Override
    @Nullable
    protected Object amendValue(@NotNull Field field, @NotNull Annotation[] annos, @Nullable Object obj) {
        if (obj instanceof String) {
            return messageSource.getMessage((String) obj, Null.Objects, localeSupplier.get());
        }
        if (obj instanceof I18nString) {
            final I18nString s = (I18nString) obj;
            final String n = messageSource.getMessage(s.getI18nCode(), s.getI18nArgs(), localeSupplier.get());
            s.setI18n(n);
            return s;
        }
        return obj;
    }
}

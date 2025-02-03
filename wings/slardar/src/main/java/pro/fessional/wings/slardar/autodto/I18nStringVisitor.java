package pro.fessional.wings.slardar.autodto;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.anti.BeanVisitor;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.i18n.I18nAware.I18nSource;
import pro.fessional.mirana.i18n.I18nMessage;
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

    private final I18nSource i18nSource;
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
        if (obj instanceof String str) {
            return i18nSource.getMessage(str, Null.Objects, str, localeSupplier.get());
        }
        if (obj instanceof final I18nString str) {
            str.setI18nCacheBy(localeSupplier.get(), i18nSource);
        }
        else if (obj instanceof I18nMessage msg) {
            msg.setMessageBy(localeSupplier.get(), i18nSource);
        }

        return obj;
    }
}

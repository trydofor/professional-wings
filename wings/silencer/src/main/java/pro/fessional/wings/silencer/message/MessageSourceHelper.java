package pro.fessional.wings.silencer.message;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import pro.fessional.mirana.i18n.I18nAware;
import pro.fessional.mirana.i18n.I18nAware.I18nSource;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2023-11-24
 */
public class MessageSourceHelper {

    public static final MessageSource Dummy = new MessageSource() {
        @Override
        public String getMessage(@NotNull String code, Object[] args, String defaultMessage, @NotNull Locale locale) {
            throw new IllegalStateException("should bind before using");
        }

        @Override
        @NotNull
        public String getMessage(@NotNull String code, Object[] args, @NotNull Locale locale) throws NoSuchMessageException {
            throw new IllegalStateException("should bind before using");
        }

        @Override
        @NotNull
        public String getMessage(@NotNull MessageSourceResolvable resolvable, @NotNull Locale locale) throws NoSuchMessageException {
            throw new IllegalStateException("should bind before using");
        }
    };

    public static final CombinableMessageSource Combine = new CombinableMessageSource();

    protected static volatile MessageSource Primary = Dummy;
    protected static volatile I18nSource i18nSource = Dummy::getMessage;
    protected static volatile boolean hasPrimary = false;
    protected static volatile boolean hasCombine = false;

    public MessageSourceHelper() {
    }

    protected MessageSourceHelper(@NotNull MessageSource primary) {
        synchronized (MessageSourceHelper.class) {
            if (primary instanceof HierarchicalMessageSource hierarchy) {
                MessageSource parent = hierarchy.getParentMessageSource();
                if (parent != null) {
                    Combine.setParentMessageSource(parent);
                }
                hierarchy.setParentMessageSource(Combine);
                hasCombine = true;
            }

            Primary = primary;
            i18nSource = primary::getMessage;
            hasPrimary = true;
        }
    }

    public static MessageSource Primary() {
        return Primary;
    }

    public static boolean hasPrimary() {
        return hasPrimary;
    }

    public static boolean hasCombine() {
        return hasCombine;
    }

    public static I18nSource i18nSource() {
        return i18nSource;
    }

    public static String getMessage(@NotNull String code, Object[] args, String defaultMessage, @NotNull Locale locale) {
        return Primary.getMessage(code, args, defaultMessage, locale);
    }

    @NotNull
    public static String getMessage(@NotNull String code, Object[] args, @NotNull Locale locale) throws NoSuchMessageException {
        return Primary.getMessage(code, args, locale);
    }

    @NotNull
    public static String getMessage(@NotNull MessageSourceResolvable resolvable, @NotNull Locale locale) throws NoSuchMessageException {
        return Primary.getMessage(resolvable, locale);
    }

    @NotNull
    public static String getMessage(@NotNull I18nAware i18nAware, @NotNull Locale locale) throws NoSuchMessageException {
        return i18nAware.toString(locale, i18nSource);
    }
}

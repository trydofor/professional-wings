package pro.fessional.wings.slardar.servlet;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2021-04-10
 */
public class MessageHelper {

    @NotNull
    public static String get(MessageSource ms, CodeEnum code, Object... args) {
        try {
            Locale locale = LocaleZoneIdUtil.LocaleNonnull.get();
            return ms.getMessage(code.getI18nCode(), args, locale);
        }
        catch (NoSuchMessageException e) {
            return Null.Str;
        }
    }
}

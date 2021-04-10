package pro.fessional.wings.slardar.servlet;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.data.Null;

/**
 * @author trydofor
 * @since 2021-04-10
 */
public class MessageHelper {

    @NotNull
    public static String get(MessageSource ms, CodeEnum code, Object... args) {
        try {
            return ms.getMessage(code.getCode(), args, LocaleContextHolder.getLocale());
        }
        catch (NoSuchMessageException e) {
            return Null.Str;
        }
    }
}

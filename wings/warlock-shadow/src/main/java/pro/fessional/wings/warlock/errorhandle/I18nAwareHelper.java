package pro.fessional.wings.warlock.errorhandle;


import org.jetbrains.annotations.NotNull;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import pro.fessional.mirana.i18n.I18nNotice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author trydofor
 * @since 2025-01-26
 */
public class I18nAwareHelper {

    @NotNull
    public static List<I18nNotice> notices(@NotNull BindingResult error) {
        List<ObjectError> errors = error.getAllErrors();
        List<I18nNotice> notices = new ArrayList<>(errors.size() + 1);
        final String type = I18nNotice.Type.Validation.name();

        for (ObjectError err : errors) {
            I18nNotice ntc = new I18nNotice();
            ntc.setType(type);
            ntc.setMessage(err.getDefaultMessage());
            ntc.setI18nCode(err.getCode());
            ntc.setI18nArgs(err.getArguments());

            if (err instanceof FieldError fe) {
                ntc.setTarget(fe.getField());
            }
            notices.add(ntc);
        }
        return notices;
    }
}

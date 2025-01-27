package pro.fessional.wings.warlock.security.error;

import lombok.Getter;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import pro.fessional.mirana.data.DataAware;
import pro.fessional.mirana.i18n.CodeEnum;
import pro.fessional.mirana.i18n.I18nAware;

/**
 * @author trydofor
 * @see ProviderManager#authenticate(Authentication)
 * @since 2023-07-10
 */
@Getter
public class FailureWaitingInternalAuthenticationServiceException extends InternalAuthenticationServiceException implements DataAware<Integer>, I18nAware {

    private final Integer data;
    private final String i18nCode;
    private final String i18nHint;
    private final Object[] i18nArgs;

    public FailureWaitingInternalAuthenticationServiceException(Throwable cause, int wait, String code, String hint, Object... args) {
        super(hint, cause);
        this.data = wait;
        this.i18nCode = code;
        this.i18nHint = hint;
        this.i18nArgs = args;
    }

    public FailureWaitingInternalAuthenticationServiceException(int wait, String code, String hint, Object... args) {
        super(hint);
        this.data = wait;
        this.i18nCode = code;
        this.i18nHint = hint;
        this.i18nArgs = args;
    }

    public FailureWaitingInternalAuthenticationServiceException(int wait, CodeEnum code, Object... args) {
        this(wait, code.getCode(), code.getHint(), args);
    }
}

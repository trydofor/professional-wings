package pro.fessional.wings.warlock.security.error;

import lombok.Getter;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import pro.fessional.mirana.data.DataResult;

/**
 * @author trydofor
 * @see ProviderManager#authenticate(Authentication)
 * @since 2023-07-10
 */
@Getter
public class FailureWaitingInternalAuthenticationServiceException extends InternalAuthenticationServiceException implements DataResult<Integer> {

    private final String code;
    private final Integer data;

    public FailureWaitingInternalAuthenticationServiceException(int wait, String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.data = wait;
    }

    public FailureWaitingInternalAuthenticationServiceException(int wait, String code, String message) {
        super(message);
        this.code = code;
        this.data = wait;
    }
}

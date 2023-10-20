package pro.fessional.wings.slardar.context;

import org.springframework.security.core.AuthenticationException;

/**
 * @author trydofor
 * @since 2023-10-2023/10/17
 */
public class SecurityContextException extends AuthenticationException {
    public SecurityContextException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SecurityContextException(String msg) {
        super(msg);
    }
}

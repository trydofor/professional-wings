package pro.fessional.wings.slardar.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * @author trydofor
 * @since 2021-02-01
 */
public class TestLoginHandler {

    private static final Logger logger = LoggerFactory.getLogger(TestLoginHandler.class);

    @Setter(onMethod_ = {@Autowired})
    private ObjectMapper objectMapper;

    public AuthenticationSuccessHandler loginSuccess = (req, res, auth) -> {
        logger.info("loginSuccess");
    };

    public AuthenticationFailureHandler loginFailure = (req, res, auth) -> {
        logger.info("loginFailure");
    };

    public LogoutSuccessHandler logoutSuccess = (req, res, auth) -> {
        logger.info("logoutSuccess");
    };
}

package pro.fessional.wings.warlock.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.errcode.AuthzErrorEnum;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author trydofor
 * @since 2023-08-29
 */
@Slf4j
public class AccessFailureHandler implements AccessDeniedHandler {

    @Setter(onMethod_ = { @Autowired })
    protected ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.debug("handled accessDeniedException", accessDeniedException);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        R<?> r = R.ngCodeMessage(AuthzErrorEnum.AccessDenied);
        String body = objectMapper.writeValueAsString(r);
        ResponseHelper.writeBodyUtf8(response, body);
    }
}

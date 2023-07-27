package pro.fessional.wings.slardar.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * @author trydofor
 * @since 2021-02-17
 */
public interface WingsAuthPageHandler {

    /**
     * handle GET/POST request of login-page. eg. send SMS, Oauth redirect
     * HttpStatus.UNAUTHORIZED if it is a forward of request, otherwise HttpStatus.OK
     *
     * @param authType  auth type
     * @param mediaType null means auto-detect
     * @param request   request
     * @param response  response
     * @see org.springframework.http.MediaType
     */
    ResponseEntity<?> response(@NotNull Enum<?> authType, MediaType mediaType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response);

}

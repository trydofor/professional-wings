package pro.fessional.wings.slardar.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理login-page的get或post请求，如发送短信，oauth重定向
 *
 * @author trydofor
 * @since 2021-02-17
 */
public interface WingsAuthPageHandler {

    /**
     * 处理login-page的get或post请求，如发送短信，oauth重定向。
     * 如果ResponseEntity.status == HttpStatus.OK，表示由调用者自行处理status。
     * 如果是request的forward则HttpStatus.UNAUTHORIZED，否则HttpStatus.OK
     *
     * @param authType  登录类型
     * @param mediaType 指定内容，null是，自动根据判断
     * @param request   request
     * @param response  response
     * @see org.springframework.http.MediaType
     */
    ResponseEntity<?> response(@NotNull Enum<?> authType, MediaType mediaType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response);

}

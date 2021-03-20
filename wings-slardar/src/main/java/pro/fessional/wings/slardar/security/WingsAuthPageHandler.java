package pro.fessional.wings.slardar.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;

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
     * 处理login-page的get或post请求，如发送短信，oauth重定向
     *
     * @param authType 登录类型
     * @param mimeType 指定内容，null是，自动根据判断
     * @param request  request
     * @param response response
     * @see org.springframework.http.MediaType
     */
    ResponseEntity<?> response(@NotNull Enum<?> authType, MimeType mimeType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response);

}

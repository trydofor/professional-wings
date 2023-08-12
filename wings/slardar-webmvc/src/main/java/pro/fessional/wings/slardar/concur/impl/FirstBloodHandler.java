package pro.fessional.wings.slardar.concur.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cache2k.Cache;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.web.method.HandlerMethod;
import pro.fessional.wings.slardar.concur.FirstBlood;

/**
 * @author trydofor
 * @since 2021-03-11
 */
public interface FirstBloodHandler extends Ordered {

    /**
     * Whether the request can be handled, if accept it must be handled.
     */
    boolean accept(@NotNull HttpServletRequest request, @NotNull FirstBlood anno);

    /**
     * Handle the request and response, return whether the Captcha is successful
     */
    boolean handle(@NotNull HttpServletRequest request,
                   @NotNull HttpServletResponse response,
                   @NotNull HandlerMethod handler,
                   @NotNull Cache<Object, Object> cache,
                   @NotNull FirstBlood anno);
}

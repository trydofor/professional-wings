package pro.fessional.wings.slardar.concur.impl;

import com.github.benmanes.caffeine.cache.Cache;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.web.method.HandlerMethod;
import pro.fessional.wings.slardar.concur.FirstBlood;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-03-11
 */
public interface FirstBloodHandler extends Ordered {

    /**
     * 是否能够处理当前请求，如果accept就必须处理
     *
     * @param request HttpServletRequest
     * @param anno    FirstBlood
     * @return true 如果可以
     */
    boolean accept(@NotNull HttpServletRequest request, @NotNull FirstBlood anno);

    /**
     * 处理请求，告知通过验证
     *
     * @param request  request
     * @param response response
     * @return 是否通过验证
     */
    boolean handle(@NotNull HttpServletRequest request,
                   @NotNull HttpServletResponse response,
                   @NotNull HandlerMethod handler,
                   @NotNull Cache<Object, Object> cache,
                   @NotNull FirstBlood anno);
}

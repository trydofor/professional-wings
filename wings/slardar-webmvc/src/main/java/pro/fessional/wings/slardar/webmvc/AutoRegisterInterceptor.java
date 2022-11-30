package pro.fessional.wings.slardar.webmvc;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;

import java.util.Collections;
import java.util.List;

/**
 * 标记接口，实现此接口的Bean，会被按order自动注册mvc环境
 *
 * @author trydofor
 * @see MappedInterceptor
 * @since 2021-04-09
 */
public interface AutoRegisterInterceptor extends HandlerInterceptor, Ordered {

    @NotNull
    default List<String> getExcludePatterns() {
        return Collections.emptyList();
    }

    @NotNull
    default List<String> getIncludePatterns() {
        return Collections.emptyList();
    }
}

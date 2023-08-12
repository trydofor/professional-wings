package pro.fessional.wings.slardar.webmvc;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;

import java.util.Collections;
import java.util.List;

/**
 * The marker interface, the bean that implements it,
 * will be auto registered with the mvc environment by its order.
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

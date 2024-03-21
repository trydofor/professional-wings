package pro.fessional.wings.slardar.webmvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import pro.fessional.wings.slardar.servlet.response.view.PlainTextView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@Slf4j
@Getter
@Setter
public abstract class WingsExceptionResolver<T extends Exception> implements HandlerExceptionResolver, Ordered {

    private int order = Ordered.LOWEST_PRECEDENCE;
    protected final Class<?> acceptClass;

    protected WingsExceptionResolver() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }
        acceptClass = (Class<?>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
        log.info("WingsExceptionResolver={}, Exception={}", this.getClass().getName(), acceptClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ModelAndView resolveException(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,  @Nullable Object handler,
            @NotNull Exception ex) {

        if (acceptClass.isInstance(ex)) {
            final SimpleResponse body = resolve((T) ex);
            if (body == null) return null;

            ModelAndView mav = new ModelAndView();
            PlainTextView pv = new PlainTextView(body.getContentType(), body.getResponseBody());
            mav.setStatus(HttpStatus.valueOf(body.getHttpStatus()));
            mav.setView(pv);
            return mav;
        }

        return null;
    }

    /**
     * Resolve Exception
     *
     * @param ex current exception
     * @return null if not support
     */
    @Nullable
    protected abstract SimpleResponse resolve(@NotNull T ex);
}

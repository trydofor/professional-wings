package pro.fessional.wings.slardar.concur.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import pro.fessional.wings.slardar.concur.DoubleKillException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2021-03-10
 */
public class DoubleKillExceptionResolver extends AbstractHandlerExceptionResolver {

    private final Function<DoubleKillException, ModelAndView> modelAndView;

    public DoubleKillExceptionResolver(Function<DoubleKillException, ModelAndView> modelAndView) {
        this.modelAndView = modelAndView;
    }

    @Override
    protected ModelAndView doResolveException(@NotNull HttpServletRequest request,
                                              @NotNull HttpServletResponse response, Object handler,
                                              @NotNull Exception ex) {
        return ex instanceof DoubleKillException ? modelAndView.apply((DoubleKillException) ex) : null;
    }
}

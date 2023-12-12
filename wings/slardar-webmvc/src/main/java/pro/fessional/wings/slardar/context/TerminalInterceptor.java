package pro.fessional.wings.slardar.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.slardar.context.TerminalContext.Builder;
import pro.fessional.wings.slardar.context.TerminalContext.Context;
import pro.fessional.wings.slardar.webmvc.AutoRegisterInterceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static pro.fessional.wings.slardar.constants.SlardarServletConst.AttrTerminalLogin;

/**
 * @author trydofor
 * @since 2019-11-16
 */
@Slf4j
@Getter
public class TerminalInterceptor implements AutoRegisterInterceptor {

    public static final int ORDER = WingsOrdered.Lv4Application + 5_000;

    private final List<TerminalBuilder> terminalBuilders = new ArrayList<>();
    private final List<TerminalLogger> terminalLoggers = new ArrayList<>();

    @Setter
    private int order = ORDER;

    @Setter
    @NotNull
    private List<String> excludePatterns = Collections.emptyList();

    public void addTerminalBuilder(TerminalBuilder builder) {
        if (builder != null) {
            terminalBuilders.add(builder);
        }
    }

    public void addTerminalLogger(TerminalLogger logger) {
        if (logger != null) {
            terminalLoggers.add(logger);
        }
    }

    /**
     * Login terminal, which must appear as a try-finally with logoutTerminal.
     */
    @NotNull
    public Context loginTerminal(@NotNull HttpServletRequest request) {
        try {
            final Builder builder = new Builder();
            for (TerminalBuilder build : terminalBuilders) {
                build.build(builder, request);
            }

            if (request.getAttribute(AttrTerminalLogin) == Boolean.TRUE) {
                log.warn("should NOT loginTerminal more than once");
            }
            else {
                request.setAttribute(AttrTerminalLogin, Boolean.TRUE);
            }

            final Context ctx = builder.build();
            TerminalContext.login(ctx);
            return ctx;
        }
        catch (RuntimeException e) {
            log.error("should NOT be here", e);
            TerminalContext.logout();
            throw e;
        }
    }

    /**
     * Logout terminal, and return the previous context if logined successfully
     */
    @Nullable
    public Context logoutTerminal(@NotNull HttpServletRequest request) {
        if (request.getAttribute(AttrTerminalLogin) == Boolean.TRUE) {
            Context ctx = TerminalContext.logout();
            request.removeAttribute(AttrTerminalLogin);
            return ctx;
        }
        else {
            return null;
        }
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        final Context ctx = loginTerminal(request);
        for (TerminalLogger log : terminalLoggers) {
            try {
                log.log(true, ctx, request, response, null);
            }
            catch (Exception e) {
                DummyBlock.ignore(e);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) {
        final Context ctx = logoutTerminal(request);
        for (TerminalLogger log : terminalLoggers) {
            try {
                log.log(false, ctx, request, response, ex);
            }
            catch (Exception e) {
                DummyBlock.ignore(e);
            }
        }
    }

    public interface TerminalBuilder {
        void build(@NotNull Builder builder, @NotNull HttpServletRequest request);
    }

    public interface TerminalLogger {
        /**
         * @param login    login or logout
         * @param context  the context
         * @param request  the request
         * @param response the response
         * @param ex       the exception if throw in afterCompletion
         */
        void log(boolean login, @Nullable Context context, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @Nullable Exception ex);
    }
}

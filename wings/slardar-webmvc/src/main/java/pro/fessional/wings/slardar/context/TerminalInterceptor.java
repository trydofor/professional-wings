package pro.fessional.wings.slardar.context;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.constants.SlardarOrderConst;
import pro.fessional.wings.slardar.context.TerminalContext.Builder;
import pro.fessional.wings.slardar.context.TerminalContext.Context;
import pro.fessional.wings.slardar.webmvc.AutoRegisterInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static pro.fessional.wings.slardar.constants.SlardarServletConst.AttrTerminalLogin;

/**
 * @author trydofor
 * @since 2019-11-16
 */
@Slf4j
public class TerminalInterceptor implements AutoRegisterInterceptor {

    @Getter
    private final List<TerminalBuilder> terminalBuilders = new ArrayList<>();

    @Setter @Getter
    @NotNull
    private List<String> excludePatterns = Collections.emptyList();

    public void addTerminalBuilder(TerminalBuilder builder) {
        if (builder != null) {
            terminalBuilders.add(builder);
        }
    }

    @NotNull
    public Builder buildTerminal(@NotNull HttpServletRequest request) {
        final Builder builder = new Builder();
        for (TerminalBuilder build : terminalBuilders) {
            build.build(builder, request);
        }
        return builder;
    }

    /**
     * 登录terminal，必须和logoutTerminal 以 try-finally形式出现。
     */
    public Context loginTerminal(@NotNull HttpServletRequest request, @NotNull Builder builder) {
        try {
            final Context ctx = builder.build();
            TerminalContext.login(ctx);
            request.setAttribute(AttrTerminalLogin, Boolean.TRUE);
            return ctx;
        }
        catch (Exception e) {
            log.error("should NOT be here", e);
            TerminalContext.logout();
            return null;
        }
    }

    /**
     * 登出，返回之前是否成功login
     */
    public boolean logoutTerminal(@NotNull HttpServletRequest request) {
        if (request.getAttribute(AttrTerminalLogin) == Boolean.TRUE) {
            TerminalContext.logout();
            return true;
        }
        else {
            return false;
        }
    }

    @Getter @Setter
    private int order = SlardarOrderConst.OrderTerminalInterceptor;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        final Builder builder = buildTerminal(request);
        return loginTerminal(request, builder) != null;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) {
        logoutTerminal(request);
    }

    public interface TerminalBuilder {
        void build(@NotNull Builder builder, @NotNull HttpServletRequest request);
    }
}

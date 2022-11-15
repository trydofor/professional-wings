package pro.fessional.wings.slardar.context;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.constants.SlardarOrderConst;
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

    @Getter @Setter
    private int order = SlardarOrderConst.OrderTerminalInterceptor;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        try {
            final TerminalContext.Builder builder = new TerminalContext.Builder();
            for (TerminalBuilder build : terminalBuilders) {
                build.build(builder, request);
            }
            request.setAttribute(AttrTerminalLogin, Boolean.TRUE);
            TerminalContext.login(builder);
        }
        catch (Exception e) {
            log.error("should NOT be here", e);
            TerminalContext.logout();
            return false;
        }
        //
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) {
        if (request.getAttribute(AttrTerminalLogin) == Boolean.TRUE) {
            TerminalContext.logout();
        }
    }

    public interface TerminalBuilder {
        void build(@NotNull TerminalContext.Builder builder, @NotNull HttpServletRequest request);
    }
}

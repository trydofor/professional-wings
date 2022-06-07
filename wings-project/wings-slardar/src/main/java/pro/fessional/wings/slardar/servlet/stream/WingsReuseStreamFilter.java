package pro.fessional.wings.slardar.servlet.stream;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import pro.fessional.wings.slardar.servlet.WingsServletConst;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author trydofor
 * @since 2019-11-14
 */
public class WingsReuseStreamFilter extends OncePerRequestFilter implements Ordered {

    @Setter @Getter
    private int order = WingsServletConst.ORDER_FILTER_RESTREAM;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull FilterChain chain)
            throws ServletException, IOException {

        ReuseStreamRequestWrapper request = new ReuseStreamRequestWrapper(req);
        ReuseStreamResponseWrapper response = new ReuseStreamResponseWrapper(res);
        chain.doFilter(request, response);
        response.copyBodyToResponse();
    }
}

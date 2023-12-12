package pro.fessional.wings.slardar.servlet.stream;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import pro.fessional.wings.silencer.spring.WingsOrdered;

import java.io.IOException;

/**
 * @author trydofor
 * @since 2019-11-14
 */
@Setter @Getter
public class WingsReuseStreamFilter extends OncePerRequestFilter implements Ordered {


    private int order = WingsOrdered.Lv4Application;
    private RequestResponseLogging requestResponseLogging;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull FilterChain chain)
            throws ServletException, IOException {

        ReuseStreamRequestWrapper request = new ReuseStreamRequestWrapper(req);
        ReuseStreamResponseWrapper response = new ReuseStreamResponseWrapper(res);

        final RequestResponseLogging.Conf cnf;
        if (requestResponseLogging != null) {
            cnf = requestResponseLogging.loggingConfig(request);
            if (cnf != null) {
                if (cnf.isRequestEnable()) {
                    if (cnf.isRequestPayload()) {
                        request.circleInputStream(false);
                    }
                    requestResponseLogging.beforeRequest(cnf, request);
                }

                if (cnf.isResponseEnable() && cnf.isResponsePayload()) {
                    response.cachingOutputStream(false);
                }
            }
        }
        else {
            cnf = null;
        }

        try {
            chain.doFilter(request, response);
        }
        finally {
            if (cnf != null && cnf.isResponseEnable()) {
                requestResponseLogging.afterResponse(cnf, request, response);
            }
        }

        response.copyBodyToResponse();
    }
}

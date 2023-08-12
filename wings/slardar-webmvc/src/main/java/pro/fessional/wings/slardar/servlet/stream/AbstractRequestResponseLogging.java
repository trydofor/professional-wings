package pro.fessional.wings.slardar.servlet.stream;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.cast.TypedCastUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;

/**
 * <rpe>
 * Prefix - feature token, such as jvmId.
 * Body - Must enable reuse Stream or caching.
 * </rpe>
 *
 * @author trydofor
 * @since 2022-06-07
 */
@Slf4j
public abstract class AbstractRequestResponseLogging implements RequestResponseLogging {

    @Setter @Getter
    private String beforeRequestPrefix = "BeforeRequest:::";
    @Setter @Getter
    private String afterResponsePrefix = "AfterResponse:::";

    @Setter @Getter
    public static class Condition extends Conf {
        private boolean requestQuery = true;
        private boolean requestClient = true;
        private Predicate<String> requestHeader;
        private Predicate<String> responseHeader;
    }

    @Override
    public abstract Condition loggingConfig(@NotNull ReuseStreamRequestWrapper req);

    @Override
    public void beforeRequest(@NotNull Conf cnf, @NotNull ReuseStreamRequestWrapper req) {
        if(!cnf.isRequestLogAfter()) {
            final String msg = createRequestMessage((Condition) cnf, req);
            logging(msg);
        }
    }

    @Override
    public void afterResponse(@NotNull Conf cnf, @NotNull ReuseStreamRequestWrapper req, @NotNull ReuseStreamResponseWrapper res) {
        if(cnf.isRequestLogAfter()) {
            final String reqMsg = createRequestMessage((Condition) cnf, req);
            logging(reqMsg);
        }

        final String resMsg = createResponseMessage((Condition) cnf, req, res);
        logging(resMsg);
    }

    protected String createRequestMessage(@NotNull Condition cnf, @NotNull ReuseStreamRequestWrapper request) {
        StringBuilder msg = new StringBuilder();
        msg.append(beforeRequestPrefix);
        msg.append(request.getRequestSeq()).append('#');
        msg.append(request.getMethod()).append(' ');
        msg.append(request.getRequestURI());

        if (cnf.isRequestQuery()) {
            buildRequestQuery(request, msg);
        }

        if (cnf.isRequestClient()) {
            buildRequestClient(request, msg);
        }

        final Predicate<String> headCond = cnf.getRequestHeader();
        if (headCond != null) {
            buildRequestHeader(request, msg, headCond);
        }

        if (cnf.isRequestPayload()) {
            buildRequestPayload(request, msg);
        }

        return msg.toString();
    }

    @SneakyThrows
    protected void buildRequestPayload(@NotNull ReuseStreamRequestWrapper request, @NotNull StringBuilder msg) {
        request.circleInputStream(false);
        final byte[] buf = request.getInputStream().readAllBytes();
        msg.append(", body=").append(new String(buf, request.getCharacterEncoding()));
    }

    protected void buildRequestHeader(@NotNull ReuseStreamRequestWrapper request, @NotNull StringBuilder msg, @NotNull Predicate<String> headCond) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String hd = names.nextElement();
            if (headCond.test(hd)) {
                final Enumeration<String> vl = request.getHeaders(hd);
                headers.addAll(hd, Collections.list(vl));
            }
        }
        if (!headers.isEmpty()) {
            msg.append(", headers=").append(headers);
        }
    }

    protected void buildRequestClient(@NotNull ReuseStreamRequestWrapper request, @NotNull StringBuilder msg) {
        String addr = request.getRemoteAddr();
        if (StringUtils.hasLength(addr)) {
            msg.append(", addr=").append(addr);
        }
        HttpSession sid = request.getSession(false);
        if (sid != null) {
            msg.append(", sid=").append(sid.getId());
        }
        String user = request.getRemoteUser();
        if (user != null) {
            msg.append(", user=").append(user);
        }
    }

    protected void buildRequestQuery(@NotNull ReuseStreamRequestWrapper request, @NotNull StringBuilder msg) {
        String qs = request.getQueryString();
        if (qs != null) {
            msg.append('?').append(qs);
        }
    }

    protected String createResponseMessage(@NotNull Condition cnf, @NotNull ReuseStreamRequestWrapper request, @NotNull ReuseStreamResponseWrapper response) {
        StringBuilder msg = new StringBuilder();
        msg.append(afterResponsePrefix);
        msg.append(request.getRequestSeq()).append('#');
        msg.append(response.getStatus()).append(' ');
        msg.append(response.getContentType());

        final Predicate<String> headCond = cnf.getResponseHeader();
        if (headCond != null) {
            buildResponseHeader(response, msg, headCond);
        }

        if (cnf.isResponsePayload()) {
            buildResponsePayload(response, msg);
        }

        return msg.toString();
    }

    @SneakyThrows
    protected void buildResponsePayload(ReuseStreamResponseWrapper response, StringBuilder msg) {
        response.cachingOutputStream(false);
        final byte[] buf = response.getContentAsByteArray();
        msg.append(", body=").append(new String(buf, response.getCharacterEncoding()));
    }

    protected void buildResponseHeader(@NotNull ReuseStreamResponseWrapper response, @NotNull StringBuilder msg, @NotNull Predicate<String> headCond) {
        HttpHeaders headers = new HttpHeaders();
        Collection<String> names = response.getHeaderNames();
        for (String hd : names) {
            if (headCond.test(hd)) {
                final List<String> vl = TypedCastUtil.castList(response.getHeaders(hd));
                headers.addAll(hd, vl);
            }
        }
        if (!headers.isEmpty()) {
            msg.append(", headers=").append(headers);
        }
    }

    protected void logging(@NotNull String message) {
        log.info(message);
    }
}

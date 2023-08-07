package pro.fessional.wings.slardar.httprest.okhttp;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.jackson.JacksonHelper;

/**
 * Traditional Post-Form Login
 *
 * @author trydofor
 * @since 2022-11-26
 */
@Setter @Getter
@Slf4j
public class OkHttpTokenizeLogin implements OkHttpTokenClient.Tokenize {

    /**
     * Parameter name of username.
     */
    private String keyUsername = "username";
    /**
     * Parameter name of password.
     */
    private String keyPassword = "password";
    /**
     * The key used when parsing the token, which by default is equal to headerAuth
     */
    private String keyToken;

    /**
     * Url to login
     */
    private String loginUrl;
    /**
     * login username
     */
    private String username;
    /**
     * login password
     */
    private String password;

    /**
     * Set the header name of token. Note, it's not a cookie, cookie will be auto-completed
     */
    private String headerAuth;

    /**
     * Whether to use auto cookie mode, default false.
     * If true, do auto login only if only response is 401,
     * other actions are handled by the cookie mechanism.
     */
    private boolean cookieAuto = false;

    private String headerAccept = "application/json";
    private String headerUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36";

    private transient String token;

    @Override
    public boolean needToken(@NotNull Request request) {
        if (cookieAuto) return false;

        final String ah = request.header(headerAuth);
        return ah == null;
    }

    @Override
    public boolean fillToken(Request.Builder builder) {
        if (cookieAuto) return true;

        final String tkn = token;
        if (tkn == null) return false;

        builder.header(headerAuth, tkn);
        return true;
    }

    @SneakyThrows
    @Override
    public boolean initToken(@NotNull Call.Factory callFactory) {
        final FormBody.Builder builder = buildForm(new FormBody.Builder())
                .add(keyUsername, username)
                .add(keyPassword, password);

        final Request request = buildRequest(new Request.Builder())
                .header("Accept", headerAccept)
                .header("User-Agent", headerUserAgent)
                .url(loginUrl)
                .post(builder.build())
                .build();

        final Response res = OkHttpClientHelper.execute(callFactory, request, false);

        if (cookieAuto) return true;

        final String tkn = parseResponse(res);
        if (tkn != null) {
            token = tkn;
        }
        return tkn != null;
    }

    @Contract("_->param1")
    protected FormBody.Builder buildForm(@NotNull FormBody.Builder builder) {
        return builder;
    }

    @Contract("_->param1")
    protected Request.Builder buildRequest(@NotNull Request.Builder builder) {
        return builder;
    }

    @SneakyThrows
    protected String parseResponse(@NotNull Response res) {
        final String hd = res.header(headerAuth);
        if (hd != null) return hd;

        final ResponseBody body = res.body();
        if (body == null) return null;

        return parseBody(body.string());
    }

    @SneakyThrows
    protected String parseBody(String str) {
        if (str == null) return null;
        if (headerAccept.contains("json") || headerAccept.contains("xml")) {
            final JsonNode node = JacksonHelper.object(str);
            return JacksonHelper.getString(node, headerAuth, null);
        }
        else {
            return null;
        }
    }
}

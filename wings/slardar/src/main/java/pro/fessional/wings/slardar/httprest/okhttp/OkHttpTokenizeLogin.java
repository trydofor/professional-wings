package pro.fessional.wings.slardar.httprest.okhttp;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.jackson.JacksonHelper;

import java.util.Map;

/**
 * 传统的Form登录
 *
 * @author trydofor
 * @since 2022-11-26
 */
@Setter @Getter
@Slf4j
public class OkHttpTokenizeLogin implements OkHttpTokenClient.Tokenize {

    /**
     * 登录用户的参数名
     */
    private String keyUsername = "username";
    /**
     * 登录密码的参数名
     */
    private String keyPassword = "password";
    /**
     * 解析token时，使用的key，默认等于headerAuth
     */
    private String keyToken;

    /**
     * 登录网址
     */
    private String loginUrl;
    /**
     * 登录的用户名
     */
    private String username;
    /**
     * 登录的密码
     */
    private String password;

    /**
     * 设置token的header名。注意不是cookie，cookie会按cookie自动完成
     */
    private String headerAuth;

    /**
     * 是否为自动的Cookie模式，默认false。
     * true时，仅401进行login，而其他动作交给cookie机制处理
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
    public boolean initToken(@NotNull OkHttpClient client) {
        final FormBody.Builder builder = buildForm(new FormBody.Builder())
                .add(keyUsername, username)
                .add(keyPassword, password);

        final Request request = buildRequest(new Request.Builder())
                .header("Accept", headerAccept)
                .header("User-Agent", headerUserAgent)
                .url(loginUrl)
                .post(builder.build())
                .build();

        final Response res = OkHttpClientHelper.execute(client, request, false);

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
            final Map<?, ?> map = JacksonHelper.object(str, Map.class);
            return (String) map.get(headerAuth);
        }
        else {
            return null;
        }
    }
}

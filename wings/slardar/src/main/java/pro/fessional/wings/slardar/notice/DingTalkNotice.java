package pro.fessional.wings.slardar.notice;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import pro.fessional.mirana.best.ArgsAssert;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.text.JsonTemplate;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.silencer.notice.SmallNotice;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;
import pro.fessional.wings.slardar.spring.prop.SlardarDingNoticeProp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME;

/**
 * <a href="https://open.dingtalk.com/document/robots/custom-robot-access">custom-robot-access</a>
 *
 * @author trydofor
 * @since 2022-09-29
 */
@RequiredArgsConstructor
@Slf4j
public class DingTalkNotice implements SmallNotice<DingTalkConf>, InitializingBean {

    @NotNull
    private final Call.Factory callFactory;
    @NotNull
    private final SlardarDingNoticeProp configProp;

    private final ConcurrentHashMap<String, DingTalkConf> dynamicConfig = new ConcurrentHashMap<>();

    @Setter(onMethod_ = {@Autowired(required = false), @Qualifier(DEFAULT_TASK_SCHEDULER_BEAN_NAME)})
    private Executor executor;

    @Override
    @NotNull
    public DingTalkConf defaultConfig() {
        return configProp.getDefault();
    }

    @Override
    public DingTalkConf combineConfig(@Nullable DingTalkConf that) {
        final DingTalkConf newConf = new DingTalkConf();
        newConf.adopt(that);
        newConf.merge(configProp.getDefault());
        return newConf;
    }

    @Override
    @Contract("_,true->!null")
    public DingTalkConf provideConfig(@Nullable String name, boolean combine) {
        if (name == null || name.isEmpty()) {
            return defaultConfig();
        }

        DingTalkConf conf = dynamicConfig.get(name);
        if (conf == null) {
            conf = configProp.get(name);
        }

        if (combine) {
            return combineConfig(conf);
        }
        else {
            return conf;
        }
    }

    /**
     * dynamic put a config, and its name can not be null
     *
     * @param config dynamic config
     * @param name  config name
     */
    public void putMailConfig(@NotNull DingTalkConf config, @NotNull String name) {
        ArgsAssert.notNull(name, "config.name");

        final DingTalkConf st = configProp.get(name);
        if (st != null) {
            config.merge(st);
        }

        dynamicConfig.put(name, config);
    }

    /**
     * delete dynamic config by name
     *
     * @param name name
     */
    public void delMailConfig(@NotNull String name) {
        dynamicConfig.remove(name);
    }

    @SneakyThrows
    @Override
    public boolean send(DingTalkConf config, String subject, String content) {
        if (subject == null && content == null) return false;

        if (config == null) {
            config = defaultConfig();
        }

        /*
        curl 'https://oapi.dingtalk.com/robot/send?access_token=xxxxxxxx' \
         -H 'Content-Type: application/json' \
         -d '
         {"msgtype":"markdown","markdown":{
             "title":"杭州天气",
             "text": " \n"
         },"at":{"isAtAll":true}}'
         */
        final String accessToken = config.getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            return false;
        }

        String host;
        final String webhookUrl = config.getWebhookUrl();
        if (webhookUrl.contains(accessToken)) {
            host = webhookUrl;
        }
        else {
            host = webhookUrl + accessToken;
        }

        final String digestSecret = config.getDigestSecret();
        if (digestSecret != null && !digestSecret.isEmpty()) {
            final long now = ThreadNow.millis();
            String stringToSign = now + "\n" + digestSecret;
            final Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            hmacSHA256.init(new SecretKeySpec(digestSecret.getBytes(UTF_8), "HmacSHA256"));
            byte[] signData = hmacSHA256.doFinal(stringToSign.getBytes(UTF_8));
            hmacSHA256.reset();
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), UTF_8);
            host = host + "&timestamp=" + now + "&sign=" + sign;
        }

        final String message;
        if ("markdown".equalsIgnoreCase(config.getMsgType())) {
            message = buildMarkdown(config, subject, content);
        }
        else {
            message = buildText(config, subject, content);
        }

        log.debug("ding-talk post message, host={}, text={}", host, message);
        final String s = OkHttpClientHelper.postJson(callFactory, host, message);
        log.debug("ding-talk result={}", s);
        return s.contains("\"errcode\":0,");
    }

    @Override
    public boolean post(DingTalkConf config, String subject, String content) {
        try {
            return send(config, subject, content);
        }
        catch (Exception e) {
            log.error("failed to post dingtalk notice", e);
            return false;
        }
    }

    @Override
    public void emit(DingTalkConf config, String subject, String content) {
        executor.execute(() -> send(config, subject, content));
    }

    @Override
    public void afterPropertiesSet() {
        if (executor == null) {
            log.warn("should reuse autowired thread pool");
            executor = Executors.newSingleThreadExecutor();
        }
    }

    /**
     * {
     * "text": {
     * "content":"我就是我, @XXX 是不一样的烟火"
     * },
     * "msgtype":"text",
     * "at": {
     * "atMobiles": [
     * "150XXXXXXXX"
     * ],
     * "isAtAll": false
     * }
     * }
     */
    public String buildText(DingTalkConf conf, String subject, String content) {
        if (subject == null) subject = Null.Str;
        if (content == null) content = Null.Str;
        final String message = subject + content;
        return JsonTemplate.obj(t -> t
                .putVal("msgtype", "text")
                .putObj("text", o -> o.putVal("content", buildContent(conf, message)))
                .putObj("at", o -> buildNotice(conf, o))
        );
    }

    /**
     * <pre>
     * {
     * "msgtype": "markdown",
     * "markdown": {
     * "title":"杭州天气",
     * "text": "#### 杭州天气 @150XXXXXXXX \n > 9度，西北风1级，空气良89，相对温度73%\n > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n > ###### 10点20分发布 [天气](https://www.dingtalk.com) \n"
     * }
     * }
     * </pre>
     */
    @SuppressWarnings("JavadocLinkAsPlainText")
    public String buildMarkdown(DingTalkConf conf, String subject, String content) {
        return JsonTemplate.obj(t -> t
                .putVal("msgtype", "markdown")
                .putObj("markdown", o -> o
                        .putVal("title", subject != null ? subject : "untitled")
                        .putVal("text", buildContent(conf, content, subject)))
                .putObj("at", o -> buildNotice(conf, o))
        );
    }

    private String buildContent(DingTalkConf conf, String main, String... kws) {
        if (main == null) main = Null.Str;

        StringBuilder sb = new StringBuilder();
        final String kw = conf.getNoticeKeyword();
        if (kw != null && !kw.isEmpty()) {
            boolean ng = !main.contains(kw);
            if (ng && kws != null) {
                for (String s : kws) {
                    if (s != null && s.contains(kw)) {
                        ng = false;
                        break;
                    }
                }
            }
            if (ng) {
                sb.append(kw);
            }
        }
        for (String mb : conf.getNoticeMobiles().values()) {
            if (!main.contains(mb)) {
                sb.append(" @").append(mb);
            }
        }

        return sb.length() == 0 ? main : main + "\n\n" + sb;
    }

    private void buildNotice(DingTalkConf conf, JsonTemplate.Obj obj) {
        if (conf.getNoticeMobiles().isEmpty()) {
            obj.putVal("isAtAll", true);
        }
        else {
            obj.putArr("atMobiles", conf.getNoticeMobiles().values());
        }
    }

}

package pro.fessional.wings.slardar.notice;

import lombok.Getter;
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
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.text.JsonTemplate;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.silencer.notice.SmallNotice;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;
import pro.fessional.wings.slardar.spring.prop.SlardarDingNoticeProp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME;
import static pro.fessional.wings.slardar.notice.DingTalkConf.MsgMarkdown;
import static pro.fessional.wings.slardar.notice.DingTalkConf.MsgText;

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

    @Setter(onMethod_ = {@Autowired(required = false), @Qualifier(DEFAULT_TASK_SCHEDULER_BEAN_NAME)})
    private Executor executor;

    @Setter(onMethod_ = {@Autowired(required = false)})
    @Getter
    private List<DingTalkConf.Loader> configLoader = Collections.emptyList();

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

        DingTalkConf conf = configProp.get(name);
        if (conf == null && configLoader != null) {
            for (DingTalkConf.Loader ld : configLoader) {
                final DingTalkConf cf = ld.load(name);
                if (cf != null) {
                    conf = cf;
                    break;
                }
            }
        }

        if (combine) {
            return combineConfig(conf);
        }
        else {
            return conf;
        }
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
             "title":"HangZhou",
             "text": " \n"
         },"at":{"isAtAll":true}}'
         */

        String host = config.getValidWebhook();
        if (host == null) {
            log.warn("skip bad webhookUrl={}, AccessToken={}", config.getWebhookUrl(), config.getAccessToken());
            return false;
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
        if (MsgMarkdown.equalsIgnoreCase(config.getMsgType())) {
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
     * "content":"I am who I am, @XXX a different kind of firework."
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
        return JsonTemplate.obj(t -> t
                .putVal("msgtype", MsgText)
                .putObj("text", o -> o.putVal("content", buildContent(conf, content, subject)))
                .putObj("at", o -> buildNotice(conf, o))
        );
    }

    /**
     * <pre>
     * {
     * "msgtype": "markdown",
     * "markdown": {
     * "title":"Hangzhou Weather",
     * "text": "#### Hangzhou Weather @150XXXXXXXX \n > northwest wind force 1\n > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n > ###### 10:20 [weather](https://www.dingtalk.com) \n"
     * }
     * }
     * </pre>
     */
    @SuppressWarnings("JavadocLinkAsPlainText")
    public String buildMarkdown(DingTalkConf conf, String subject, String content) {
        return JsonTemplate.obj(t -> t
                .putVal("msgtype", MsgMarkdown)
                .putObj("markdown", o -> o
                        .putVal("title", subject != null ? subject : "untitled")
                        .putVal("text", buildContent(conf, content, subject)))
                .putObj("at", o -> buildNotice(conf, o))
        );
    }

    private String buildContent(DingTalkConf conf, String main, String title) {
        if (main == null) main = Null.Str;

        StringBuilder buff = new StringBuilder();
        // title
        if (title != null && !main.contains(title)) {
            buff.append("# ").append(title).append("\n\n")
                .append(main);
        }

        // key word
        final String kw = conf.getNoticeKeyword();
        if (kw != null && !kw.isEmpty() && !main.contains(kw)) {
            buff.append('\n').append(kw);
        }
        // notice
        for (String mb : conf.getNoticeMobiles().values()) {
            if (!main.contains(mb)) {
                buff.append(" @").append(mb);
            }
        }

        return buff.isEmpty() ? main : buff.toString();
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

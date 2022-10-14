package pro.fessional.wings.slardar.notice;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.text.JsonTemplate;
import pro.fessional.wings.slardar.httprest.OkHttpClientHelper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * https://open.dingtalk.com/document/robots/custom-robot-access
 *
 * @author trydofor
 * @since 2022-09-29
 */
@RequiredArgsConstructor
@Slf4j
public class DingTalkNotice implements SmallNotice<DingTalkNotice.Conf> {

    private final OkHttpClient okHttpClient;

    @SneakyThrows
    @Override
    public boolean send(@NotNull Conf config, @NotNull String content) {
        /*
        curl 'https://oapi.dingtalk.com/robot/send?access_token=xxxxxxxx' \
         -H 'Content-Type: application/json' \
         -d '
         {"msgtype":"markdown","markdown":{
             "title":"杭州天气",
             "text": " \n"
         },"at":{"isAtAll":true}}'
         */
        final String accessToken = config.accessToken;
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
            final long now = System.currentTimeMillis();
            String stringToSign = now + "\n" + digestSecret;
            final Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            hmacSHA256.init(new SecretKeySpec(digestSecret.getBytes(UTF_8), "HmacSHA256"));
            byte[] signData = hmacSHA256.doFinal(stringToSign.getBytes(UTF_8));
            hmacSHA256.reset();
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), UTF_8);
            host = host + "&timestamp=" + now + "&sign=" + sign;
        }

        log.debug("ding-talk post message, host={}, text={}", host, content);
        final String s = OkHttpClientHelper.postJson(okHttpClient, host, content);
        log.debug("ding-talk result={}", s);
        return s.contains("\"errcode\":0,");
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
    public String buildText(Conf conf, String text) {
        return JsonTemplate.obj(t -> t
                .putVal("msgtype", "text")
                .putObj("text", o -> o.putVal("content", buildContent(conf, text)))
                .putObj("at", o -> buildNotice(conf, o))
        );
    }

    /**
     * {
     * "msgtype": "markdown",
     * "markdown": {
     * "title":"杭州天气",
     * "text": "#### 杭州天气 @150XXXXXXXX \n > 9度，西北风1级，空气良89，相对温度73%\n > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n > ###### 10点20分发布 [天气](https://www.dingtalk.com) \n"
     * }
     * }
     */
    public String buildMarkdown(Conf conf, String title, String text) {
        return JsonTemplate.obj(t -> t
                .putVal("msgtype", "markdown")
                .putObj("markdown", o -> o
                        .putVal("title", title)
                        .putVal("text", buildContent(conf, text, title)))
                .putObj("at", o -> buildNotice(conf, o))
        );
    }

    private String buildContent(Conf conf, String main, String... ext) {
        StringBuilder sb = new StringBuilder();
        final String kw = conf.getNoticeKeyword();
        if (kw != null && !kw.isEmpty()) {
            boolean ng = !main.contains(kw);
            if (ng) {
                for (String s : ext) {
                    if (s.contains(kw)) {
                        ng = false;
                        break;
                    }
                }
            }
            if (ng) {
                sb.append(kw);
            }
        }
        for (String mb : conf.getNoticeMobiles()) {
            if (!main.contains(mb)) {
                sb.append(" @").append(mb);
            }
        }

        return sb.length() == 0 ? main : main + "\n\n" + sb;
    }

    private void buildNotice(Conf conf, JsonTemplate.Obj obj) {
        if (conf.getNoticeMobiles().isEmpty()) {
            obj.putVal("isAtAll", true);
        }
        else {
            obj.putArr("atMobiles", conf.getNoticeMobiles());
        }
    }

    @Data
    public static class Conf {
        private String webhookUrl = "";

        /**
         * 消息签名，空表示不使用
         */
        private String digestSecret = "";

        /**
         * 警报时，使用钉钉通知的access_token，空表示不使用。
         */
        private String accessToken = "";

        /**
         * 自定义关键词：最多可以设置10个关键词，消息中至少包含其中1个关键词才可以发送成功
         */
        private String noticeKeyword = "";

        /**
         * 在text内容里要有@人的手机号，只有在群内的成员才可被@，非群内成员手机号会被脱敏。
         */
        private Set<String> noticeMobiles = Collections.emptySet();

    }
}

package pro.fessional.wings.slardar.monitor.report;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;
import pro.fessional.wings.slardar.httprest.OkHttpClientHelper;
import pro.fessional.wings.slardar.monitor.WarnMetric;
import pro.fessional.wings.slardar.monitor.WarnReport;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 钉钉机器人 https://developers.dingtalk.com/document/app/custom-robot-access
 *
 * @author trydofor
 * @since 2021-07-14
 */
@Slf4j
@Getter @Setter
public class DingTalkReport implements WarnReport {

    private final Conf conf;
    private final OkHttpClient okHttpClient;

    private String clientUrl = "https://oapi.dingtalk.com/robot/send?access_token=";

    public DingTalkReport(Conf conf, OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        this.conf = conf;
    }

    @Override
    public Sts report(String title, Map<String, List<WarnMetric.Warn>> warn) {
        if (!StringUtils.hasText(conf.accessToken)) {
            log.info("accessToken is empty, skip");
            return Sts.Skip;
        }

        if (conf.accessToken.contains("${")) {
            log.info("accessToken has placeholder, skip");
            return Sts.Skip;
        }

        if (warn.isEmpty()) {
            log.info("warning is empty, skip");
            return Sts.Skip;
        }

        String text = buildMarkdown(title, sb -> {
            for (Map.Entry<String, List<WarnMetric.Warn>> entry : warn.entrySet()) {
                title(sb, entry.getKey());
                for (WarnMetric.Warn w : entry.getValue()) {
                    if (w.getType() == WarnMetric.Type.Link) {
                        link(sb, w);
                    }
                    else {
                        item(sb, w);
                    }
                }
            }
        });

        final boolean rst = post(text);
        return rst ? Sts.Done : Sts.Fail;
    }

    public String buildText(String title, String text) {
        return "{\"msgtype\": \"text\",\"text\": {\"content\":\""
               + checkKeyword(title)
               + escapeQuote(text)
               + "\"}}";
    }

    public String buildMarkdown(String title, Consumer<StringBuilder> text) {
        StringBuilder sb = new StringBuilder();
        title = checkKeyword(title);
        sb.append("{\"msgtype\":\"markdown\",\"markdown\":{");
        sb.append("\"title\":\"").append(title).append("\",");
        sb.append("\"text\":\"");
        sb.append("# ").append(escapeQuote(title)).append("\n");
        text.accept(sb);
        sb.append("\"},\"at\":{\"isAtAll\":true}}");
        return sb.toString();
    }

    public boolean post(String text) {
        /*
        curl 'https://oapi.dingtalk.com/robot/send?access_token=xxxxxxxx' \
         -H 'Content-Type: application/json' \
         -d '
         {"msgtype":"markdown","markdown":{
             "title":"杭州天气",
             "text": " \n"
         },"at":{"isAtAll":true}}'
         */
        final String host;
        if (StringUtils.hasText(conf.digestSecret)) {
            final long now = System.currentTimeMillis();
            String sign = sign(now);
            host = clientUrl + conf.accessToken + "&timestamp=" + now + "&sign=" + sign;
        }
        else {
            host = clientUrl + conf.accessToken;
        }
        log.debug("ding-talk post message, host={}, text={}", host, text);
        final String s = OkHttpClientHelper.postJson(okHttpClient, host, text);
        log.debug("ding-talk result={}", s);
        return s.contains("errcode");
    }

    private String checkKeyword(String title) {
        if (StringUtils.hasText(conf.reportKeyword) && !title.contains(conf.reportKeyword)) {
            title = title + ":" + conf.reportKeyword;
        }
        return escapeQuote(title);
    }

    private void title(StringBuilder sb, String str) {
        sb.append("## ")
          .append(escapeQuote(str))
          .append("\n");
    }

    private void item(StringBuilder sb, WarnMetric.Warn warn) {
        sb.append("- ")
          .append("**").append(warn.getWarn()).append("** | ")
          .append(escapeQuote(warn.getRule()))
          .append(" | ")
          .append(escapeQuote(warn.getKey()))
          .append("\n");
    }

    private void link(StringBuilder sb, WarnMetric.Warn warn) {
        sb.append("- [")
          .append(escapeQuote(warn.getRule()))
          .append("](")
          .append(escapeQuote(warn.getWarn()))
          .append(") | ")
          .append(escapeQuote(warn.getKey()))
          .append("\n");
    }

    private String escapeQuote(String str) {
        return str.replace("\"", "\\\"");
    }

    @SneakyThrows
    private String sign(long timestamp) {
        String stringToSign = timestamp + "\n" + conf.digestSecret;
        // 低频使用，不需要缓存，fire&forget
        final Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        hmacSHA256.init(new SecretKeySpec(conf.digestSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = hmacSHA256.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        hmacSHA256.reset();
        return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
    }

    @Data
    public static class Conf {
        public static final String Key = "wings.slardar.monitor.ding-talk";

        /**
         * 警报时，使用钉钉通知的access_token，空表示不使用。
         *
         * @see #Key$accessToken
         */
        private String accessToken = "";
        public static final String Key$accessToken = Key + ".access-token";

        /**
         * 消息签名，空表示不使用
         *
         * @see #Key$digestSecret
         */
        private String digestSecret = "";
        public static final String Key$digestSecret = Key + ".digest-secret";

        /**
         * 自定义关键词：最多可以设置10个关键词，消息中至少包含其中1个关键词才可以发送成功
         *
         * @see #Key$reportKeyword
         */
        private String reportKeyword = "";
        public static final String Key$reportKeyword = Key + ".report-keyword";
    }
}

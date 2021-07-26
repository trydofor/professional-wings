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
import java.time.ZonedDateTime;
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
    public Sts report(String appName, String jvmName, Map<String, List<WarnMetric.Warn>> warn) {
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

        String text = buildMarkdown(appName, jvmName, sb -> {
            for (Map.Entry<String, List<WarnMetric.Warn>> entry : warn.entrySet()) {
                mkTitleH2(sb, entry.getKey());
                for (WarnMetric.Warn w : entry.getValue()) {
                    if (w.getType() == WarnMetric.Type.Link) {
                        mkItemLink(sb, w);
                    }
                    else {
                        mkItemText(sb, w);
                    }
                }
            }
        });

        final boolean rst = post(text);
        return rst ? Sts.Done : Sts.Fail;
    }

    public String buildText(String app, String jvm, String text) {
        StringBuilder sb = new StringBuilder();
        if (conf.reportKeyword != null) {
            sb.append(escapeQuote(conf.reportKeyword));
        }
        final int zln = sb.length();

        sb.append("app=").append(escapeQuote(app));
        sb.append(",jvm=").append(escapeQuote(jvm));
        sb.append(",time=").append(ZonedDateTime.now());
        sb.append(",text=").append(escapeQuote(text));

        final String rst;
        if (zln > 0 && sb.indexOf(conf.reportKeyword, zln) < 0) {
            rst = sb.toString();
        }
        else {
            rst = sb.substring(zln);
        }

        return "{\"msgtype\": \"text\",\"text\": {\"content\":\""
               + rst
               + "\"}}";
    }

    public String buildMarkdown(String app, String jvm, Consumer<StringBuilder> text) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"msgtype\":\"markdown\",\"markdown\":{");
        sb.append("\"title\":\"").append(escapeQuote(conf.reportKeyword + ":" + app)).append("\",");
        sb.append("\"text\":\"");
        mkTitleH2(sb, app);
        mkItemText(sb, jvm, "jvm-name");
        mkItemText(sb, ZonedDateTime.now().toString(), "rpt-time");
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

    protected void mkTitleH2(StringBuilder sb, String str) {
        sb.append("## ■ ")
          .append(escapeQuote(str))
          .append("\n");
    }

    protected void mkItemText(StringBuilder sb, String value, String key) {
        sb.append("- ")
          .append(escapeQuote(value))
          .append(" | ")
          .append(key)
          .append("\n");
    }

    protected void mkItemText(StringBuilder sb, WarnMetric.Warn warn) {
        sb.append("- ")
          .append("**").append(warn.getWarn()).append("** | ")
          .append(escapeQuote(warn.getRule()))
          .append(" | ")
          .append(escapeQuote(warn.getKey()))
          .append("\n");
    }

    protected void mkItemLink(StringBuilder sb, WarnMetric.Warn warn) {
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

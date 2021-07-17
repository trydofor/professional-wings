package pro.fessional.wings.slardar.monitor.report;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.codec.binary.Base64;
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
 * @author trydofor
 * @since 2021-07-14
 */
@Slf4j
@Getter @Setter
public class DingTalkReport implements WarnReport {

    private final String accessToken;
    private final String dingTalkHost;
    private final OkHttpClient okHttpClient;

    private String warnKeyword;
    private String digestSecret;

    public DingTalkReport(String accessToken, OkHttpClient okHttpClient) {
        this.accessToken = accessToken;
        this.dingTalkHost = "https://oapi.dingtalk.com/robot/send?access_token=" + accessToken;
        this.okHttpClient = okHttpClient;
    }

    @Override
    public Sts report(String jvmName, Map<String, List<WarnMetric.Warn>> warn) {
        if (accessToken == null || accessToken.isEmpty()) {
            log.info("accessToken is empty, skip");
            return Sts.Skip;
        }

        if (warn.isEmpty()) {
            log.info("warning is empty, skip");
            return Sts.Skip;
        }

        StringBuilder buffer = new StringBuilder();
        buildMarkdown(buffer, jvmName, sb -> {
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

        if (warnKeyword != null && buffer.indexOf(warnKeyword) < 0) {
            buffer.append("\ndingtalk-keyword=").append(warnKeyword);
        }

        final boolean rst = post(buffer.toString());
        return rst ? Sts.Done : Sts.Fail;
    }

    public void buildMarkdown(StringBuilder sb, String title, Consumer<StringBuilder> text) {
        sb.append("{\"msgtype\":\"markdown\",\"markdown\":{");
        sb.append("\"title\":\"").append(title).append("\",");
        sb.append("\"text\":\"");
        text.accept(sb);
        sb.append("\"},\"at\":{\"isAtAll\":true}}");
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
        final long now = System.currentTimeMillis();
        final String host;
        if (digestSecret == null || digestSecret.isEmpty()) {
            host = dingTalkHost;
        }
        else {
            String sign = sign(now);
            host = dingTalkHost + "&timestamp=" + now + "&sign=" + sign;
        }
        log.debug("ding-talk post message, host={}, text={}", host, text);
        final String s = OkHttpClientHelper.postJson(okHttpClient, host, text);
        log.debug("ding-talk result={}", s);
        return s.contains("errcode");
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
        String stringToSign = timestamp + "\n" + digestSecret;
        // 低频使用，不需要缓存，fire&forget
        final Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        hmacSHA256.init(new SecretKeySpec(digestSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = hmacSHA256.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        hmacSHA256.reset();
        return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
    }
}

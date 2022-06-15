package pro.fessional.wings.slardar.monitor.report;

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
import pro.fessional.wings.slardar.spring.prop.SlardarMonitorProp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 钉钉机器人 https://developers.dingtalk.com/document/app/custom-robot-access
 *
 * @author trydofor
 * @since 2021-07-14
 */
@Slf4j
@Getter @Setter
public class DingTalkReport implements WarnReport {

    private final SlardarMonitorProp.DingTalkConf conf;
    private final OkHttpClient okHttpClient;

    public DingTalkReport(SlardarMonitorProp.DingTalkConf conf, OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        this.conf = conf;
    }

    @Override
    public Sts report(String appName, String jvmName, Map<String, List<WarnMetric.Warn>> warn) {
        final String atk = conf.getAccessToken();
        if (!StringUtils.hasText(atk)) {
            log.info("accessToken is empty, skip");
            return Sts.Skip;
        }

        if (atk.contains("${")) {
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
        final String rkw = conf.getReportKeyword();
        if (rkw != null) {
            sb.append(escapeQuote(rkw));
        }
        final int zln = sb.length();

        sb.append("app=").append(escapeQuote(app));
        sb.append(",jvm=").append(escapeQuote(jvm));
        sb.append(",time=").append(ZonedDateTime.now());
        sb.append(",text=").append(escapeQuote(text));

        final String rst;
        if (rkw != null && zln > 0 && sb.indexOf(rkw, zln) < 0) {
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
        sb.append("\"title\":\"").append(escapeQuote(conf.getReportKeyword() + ":" + app)).append("\",");
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
        if (StringUtils.hasText(conf.getDigestSecret())) {
            final long now = System.currentTimeMillis();
            String sign = sign(now);
            host = conf.getWebhookUrl() + "&timestamp=" + now + "&sign=" + sign;
        }
        else {
            host = conf.getWebhookUrl();
        }
        log.debug("ding-talk post message, host={}, text={}", host, text);
        final String s = OkHttpClientHelper.postJson(okHttpClient, host, text);
        log.debug("ding-talk result={}", s);
        return s.contains("errcode");
    }

    protected void mkTitleH2(StringBuilder sb, String str) {
        sb.append("\n\n## ■ ")
          .append(escapeQuote(str));
    }

    protected void mkItemText(StringBuilder sb, String value, String key) {
        sb.append("\n- ")
          .append(escapeQuote(value))
          .append(" | ")
          .append(key);
    }

    protected void mkItemText(StringBuilder sb, WarnMetric.Warn warn) {
        sb.append("\n- ")
          .append("**").append(warn.getWarn()).append("** | ")
          .append(escapeQuote(warn.getRule()))
          .append(" | ")
          .append(escapeQuote(warn.getKey()));
    }

    protected void mkItemLink(StringBuilder sb, WarnMetric.Warn warn) {
        sb.append("\n- [")
          .append(escapeQuote(warn.getRule()))
          .append("](")
          .append(escapeQuote(warn.getWarn()))
          .append(") | ")
          .append(escapeQuote(warn.getKey()));
    }

    private String escapeQuote(String str) {
        return str.replace("\"", "\\\"");
    }

    @SneakyThrows
    private String sign(long timestamp) {
        final String ds = conf.getDigestSecret();
        String stringToSign = timestamp + "\n" + ds;
        // 低频使用，不需要缓存，fire&forget
        final Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        hmacSHA256.init(new SecretKeySpec(ds.getBytes(UTF_8), "HmacSHA256"));
        byte[] signData = hmacSHA256.doFinal(stringToSign.getBytes(UTF_8));
        hmacSHA256.reset();
        return URLEncoder.encode(new String(Base64.encodeBase64(signData)), UTF_8);
    }
}

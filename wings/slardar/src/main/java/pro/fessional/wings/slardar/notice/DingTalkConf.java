package pro.fessional.wings.slardar.notice;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.silencer.encrypt.SecretProvider;
import pro.fessional.wings.slardar.jackson.AesString;

import java.util.HashMap;
import java.util.Map;

import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.mergeNotValue;
import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.notValue;

/**
 * @author trydofor
 * @since 2023-02-21
 */
@Data
public class DingTalkConf {

    public static final String MsgText = "text";
    public static final String MsgMarkdown = "markdown";

    /**
     * the dryrun prefix of subject. merge if null, `empty` means disable.
     */
    private String dryrun;

    /**
     * template of DingTalk webhook URL.
     */
    private String webhookUrl = "";

    /**
     * secret of message digest, `empty` means disable.
     */
    @AesString(SecretProvider.Config)
    private String digestSecret = "";

    /**
     * the DingTalk access_token used to send the alert, `empty` means disable.
     */
    @AesString(SecretProvider.Config)
    private String accessToken = "";

    /**
     * custom keywords, to successfully send  message must contain at least 1 keyword.
     */
    private String noticeKeyword = "";

    /**
     * message type, support `text`|`markdown`
     */
    private String msgType = MsgMarkdown;

    /**
     * notified person and his phone number, non-member's phone number will be desensitized.
     * It is automatically added to the text eg. @155xxxx
     */
    private Map<String, String> noticeMobiles = new HashMap<>();

    @Nullable
    public String getValidWebhook() {
        if (webhookUrl == null || webhookUrl.isEmpty()) return null;

        if (webhookUrl.endsWith("=")) {
            return (accessToken == null || accessToken.isEmpty()) ? null : webhookUrl + accessToken;
        }
        else {
            return webhookUrl;
        }
    }

    /**
     * use all properties from that
     */
    public void adopt(DingTalkConf that) {
        if (that == null) return;

        dryrun = that.dryrun;
        webhookUrl = that.webhookUrl;
        digestSecret = that.digestSecret;
        accessToken = that.accessToken;
        noticeKeyword = that.noticeKeyword;
        msgType = that.msgType;
        noticeMobiles.putAll(that.noticeMobiles);
    }

    /**
     * if this.property is invalid, then use that.property.
     * except for 'noticeMobiles' which merge value only if key matches.
     */
    public void merge(DingTalkConf that) {
        if (that == null) return;

        if (dryrun == null) dryrun = that.dryrun;
        if (notValue(webhookUrl)) webhookUrl = that.webhookUrl;
        if (notValue(digestSecret)) digestSecret = that.digestSecret;
        if (notValue(accessToken)) accessToken = that.accessToken;
        if (notValue(noticeKeyword)) noticeKeyword = that.noticeKeyword;
        if (notValue(msgType)) msgType = that.msgType;
        mergeNotValue(noticeMobiles, that.noticeMobiles);
    }

    public interface Loader {
        /**
         * load config by its name (non-empty)
         */
        @Nullable
        DingTalkConf load(@NotNull String name);
    }
}

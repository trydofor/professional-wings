package pro.fessional.wings.slardar.notice;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.cond.IfSetter;
import pro.fessional.wings.silencer.encrypt.SecretProvider;
import pro.fessional.wings.silencer.support.PropHelper;
import pro.fessional.wings.slardar.jackson.AesString;

import java.util.HashMap;
import java.util.Map;

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

    public static final IfSetter<DingTalkConf, DingTalkConf> ConfSetter = (thiz, that, absent, present) -> {
        if (that == null) return thiz;

        if (absent == IfSetter.Absent.Invalid) {
            if (thiz.dryrun == null) thiz.dryrun = that.dryrun;
            if (PropHelper.invalid(thiz.webhookUrl)) thiz.webhookUrl = that.webhookUrl;
            if (PropHelper.invalid(thiz.digestSecret)) thiz.digestSecret = that.digestSecret;
            if (PropHelper.invalid(thiz.accessToken)) thiz.accessToken = that.accessToken;
            if (PropHelper.invalid(thiz.noticeKeyword)) thiz.noticeKeyword = that.noticeKeyword;
            if (PropHelper.invalid(thiz.msgType)) thiz.msgType = that.msgType;
            PropHelper.mergeToInvalid(thiz.noticeMobiles, that.noticeMobiles);
        }
        else {
            thiz.dryrun = that.dryrun;
            thiz.webhookUrl = that.webhookUrl;
            thiz.digestSecret = that.digestSecret;
            thiz.accessToken = that.accessToken;
            thiz.noticeKeyword = that.noticeKeyword;
            thiz.msgType = that.msgType;
            thiz.noticeMobiles.putAll(that.noticeMobiles);
        }

        return thiz;
    };

    public interface Loader {
        /**
         * load config by its name (non-empty)
         */
        @Nullable
        DingTalkConf load(@NotNull String name);
    }
}

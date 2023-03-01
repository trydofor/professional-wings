package pro.fessional.wings.slardar.notice;

import lombok.Data;
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
    private String webhookUrl = "";

    /**
     * 消息签名，空表示不使用
     */
    @AesString(SecretProvider.Config)
    private String digestSecret = "";

    /**
     * 警报时，使用钉钉通知的access_token，空表示不使用。
     */
    @AesString(SecretProvider.Config)
    private String accessToken = "";

    /**
     * 自定义关键词：最多可以设置10个关键词，消息中至少包含其中1个关键词才可以发送成功
     */
    private String noticeKeyword = "";

    /**
     * 消息类型，支持 text, markdown
     */
    private String msgType = "markdown";

    /**
     * 在text内容里要有@人的手机号，只有在群内的成员才可被@，非群内成员手机号会被脱敏。
     */
    private Map<String, String> noticeMobiles = new HashMap<>();

    /**
     * use all properties from that
     */
    public void adopt(DingTalkConf that) {
        if (that == null) return;

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

        if (notValue(webhookUrl)) webhookUrl = that.webhookUrl;
        if (notValue(digestSecret)) digestSecret = that.digestSecret;
        if (notValue(accessToken)) accessToken = that.accessToken;
        if (notValue(noticeKeyword)) noticeKeyword = that.noticeKeyword;
        if (notValue(msgType)) msgType = that.msgType;
        mergeNotValue(noticeMobiles, that.noticeMobiles);
    }
}

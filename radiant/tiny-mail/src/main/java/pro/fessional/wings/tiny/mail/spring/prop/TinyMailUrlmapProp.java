package pro.fessional.wings.tiny.mail.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @since 2023-01-13
 */
@Data
@ConfigurationProperties(TinyMailUrlmapProp.Key)
public class TinyMailUrlmapProp {
    public static final String Key = "wings.tiny.mail.urlmap";

    /**
     * 获取全部邮件的简要信息，默认倒序
     *
     * @see #Key$listAll
     */
    private String listAll = "";
    public static final String Key$listAll = Key + ".list-all";

    /**
     * 获取失败邮件的简要信息，默认倒序
     *
     * @see #Key$listFailed
     */
    private String listFailed = "";
    public static final String Key$listFailed = Key + ".list-failed";

    /**
     * 获取未成功邮件的简要信息，默认倒序
     *
     * @see #Key$listUndone
     */
    private String listUndone = "";
    public static final String Key$listUndone = Key + ".list-undone";

    /**
     * 根据Biz-Mark获取邮件的简要信息，默认倒序
     *
     * @see #Key$byBizmark
     */
    private String byBizmark = "";
    public static final String Key$byBizmark = Key + ".by-bizmark";

    /**
     * 根据正则比较收件人to/cc/bcc获取邮件的简要信息，默认倒序
     *
     * @see #Key$byRecipient
     */
    private String byRecipient = "";
    public static final String Key$byRecipient = Key + ".by-recipient";

    /**
     * 根据收件人from获取邮件的简要信息，默认倒序
     *
     * @see #Key$bySender
     */
    private String bySender = "";
    public static final String Key$bySender = Key + ".by-sender";

    /**
     * 根据正则比较邮件标题获取邮件的简要信息，默认倒序
     *
     * @see #Key$bySubject
     */
    private String bySubject = "";
    public static final String Key$bySubject = Key + ".by-subject";

    /**
     * 获取邮件详情
     *
     * @see #Key$loadDetail
     */
    private String loadDetail = "";
    public static final String Key$loadDetail = Key + ".load-detail";
}

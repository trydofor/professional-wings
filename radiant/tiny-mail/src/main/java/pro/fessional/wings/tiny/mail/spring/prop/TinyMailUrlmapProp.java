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
     * list summary of all messages, in reverse order by default.
     *
     * @see #Key$listAll
     */
    private String listAll = "";
    public static final String Key$listAll = Key + ".list-all";

    /**
     * list summary of failed emails, in reverse order by default.
     *
     * @see #Key$listFailed
     */
    private String listFailed = "";
    public static final String Key$listFailed = Key + ".list-failed";

    /**
     * list summary of unsuccessful emails, in reverse order by default.
     *
     * @see #Key$listUndone
     */
    private String listUndone = "";
    public static final String Key$listUndone = Key + ".list-undone";

    /**
     * find summary of the email by Biz-Mark, in reverse order by default.
     *
     * @see #Key$byBizmark
     */
    private String byBizmark = "";
    public static final String Key$byBizmark = Key + ".by-bizmark";

    /**
     * find summary of the email by RegExp of to/cc/bcc, reverse order by default.
     *
     * @see #Key$byRecipient
     */
    private String byRecipient = "";
    public static final String Key$byRecipient = Key + ".by-recipient";

    /**
     * find summary of the email by from, in reverse order by default.
     *
     * @see #Key$bySender
     */
    private String bySender = "";
    public static final String Key$bySender = Key + ".by-sender";

    /**
     * find summary of the email by RegExp of subject, reverse order by default.
     *
     * @see #Key$bySubject
     */
    private String bySubject = "";
    public static final String Key$bySubject = Key + ".by-subject";

    /**
     * get mail detail.
     *
     * @see #Key$loadDetail
     */
    private String loadDetail = "";
    public static final String Key$loadDetail = Key + ".load-detail";

    /**
     * create or save an email, and send it immediately or asynchronously
     *
     * @see #Key$sendMail
     */
    private String sendMail = "";
    public static final String Key$sendMail = Key + ".send-mail";

    /**
     * only save messages, but do not send them.
     *
     * @see #Key$sendSave
     */
    private String sendSave = "";
    public static final String Key$sendSave = Key + ".send-save";

    /**
     * sync retry failed emails, send success or failure, or exceptions
     *
     * @see #Key$sendRetry
     */
    private String sendRetry = "";
    public static final String Key$sendRetry = Key + ".send-retry";

    /**
     * sync scan the emails that need to resend,
     * and send them async, return the number of resend emails.
     *
     * @see #Key$sendScan
     */
    private String sendScan = "";
    public static final String Key$sendScan = Key + ".send-scan";
}

package pro.fessional.wings.tiny.mail.service;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * Using basic business types, exchanging data with external
 *
 * @author trydofor
 * @since 2023-01-13
 */
@Data
public class TinyMailPlain {

    /**
     * null when creating, target id when updating
     */
    private Long id;

    /**
     * The app it belongs to, using spring.application.name when empty
     */
    private String apps;

    /**
     * Required RunMode, current RuntimeMode.getRunMode when empty
     */
    private String runs;

    /**
     * the name of the config
     */
    private String conf;

    /**
     * mail from, use the default config when empty
     */
    private String from;

    /**
     * mail to, use the default config when empty, comma separated
     */
    private String to;

    /**
     * mail cc, use the default config when empty, comma separated
     */
    private String cc;

    /**
     * mail bcc, use the default config when empty, comma separated
     */
    private String bcc;

    /**
     * mail reply, use the default config when empty
     */
    private String reply;

    /**
     * mail subject, use the default config when empty
     */
    private String subject;

    /**
     * mail content, use the default config when empty
     */
    private String content;

    /**
     * mail attachment, display name and Resource of Map, where Resource supports classpath, file, url format
     */
    private Map<String, String> attachment = Collections.emptyMap();

    /**
     * whether it is html mail, use the default config when empty
     */
    private Boolean html;

    /**
     * business markers, mainly used for failed operations
     */
    private String mark;

    /**
     * Mail timed delivery time, system time zone
     */
    private LocalDateTime date;

    /**
     * Next send time, system time zone, update when non-null
     */
    private LocalDateTime nextSend;

    /**
     * Maximum number of failures, update on non-null
     */
    private Integer maxFail;

    /**
     * Maximum number of successes, update when non-null
     */
    private Integer maxDone;

    /**
     * Reference type, indicates key1, key2 use
     */
    private Integer refType;

    /**
     * Reference key1, generally the primary key
     */
    private Long refKey1;

    /**
     * Reference key2, generally composite type
     */
    private String refKey2;

    /**
     * System time of mail creation, read-only, for display only
     */
    private LocalDateTime createDt;

    /**
     * The system time when the message was last sent, read-only, used only to display
     */
    private LocalDateTime lastSend;

    /**
     * The reason why the email failed to be sent last time, null is not failed, read only, only used to display
     */
    private String lastFail;

    /**
     * The system time when the message was last sent successfully, read-only, used only to display
     */
    private LocalDateTime lastDone;

    /**
     * Number of milliseconds since the last message was sent, read-only, for display only
     */
    private Integer lastCost;

    /**
     * Total number of emails sent, read-only, for display only
     */
    private Integer sumSend;

    /**
     * Total number of failed emails, read-only, for display only
     */
    private Integer sumFail;

    /**
     * Total number of successful emails, read-only, for display only
     */
    private Integer sumDone;

    /**
     * Sending parameters, whether to retry when sending fails
     */
    private Boolean retry;

    /**
     * Send parameter, whether to check the sending condition, otherwise it is forced to send
     */
    private Boolean check;
}

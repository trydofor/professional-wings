package pro.fessional.wings.tiny.mail.service;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.Resource;
import pro.fessional.wings.silencer.support.PropHelper;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2023-01-12
 */
@Data
public class TinyMail {
    /**
     * Config name, use default if empty
     */
    protected String conf;
    /**
     * Mail from, use default if empty
     */
    protected String from;
    /**
     * Mail to, use default if null
     */
    @Nullable
    protected String[] to;

    public void setTo(String... to) {
        this.to = to;
    }

    /**
     * Mail cc, use default if null
     */
    @Nullable
    protected String[] cc;

    public void setCc(String... cc) {
        this.cc = cc;
    }

    /**
     * Mail bcc, use default if null
     */
    @Nullable
    protected String[] bcc;

    public void setBcc(String... bcc) {
        this.bcc = bcc;
    }

    /**
     * Mail reply, use default if empty
     */
    protected String reply;

    /**
     * Mail subject, use default if empty
     */
    protected String subject;
    /**
     * Mail content, use default if empty
     */
    protected String content;
    /**
     * Mail attachment and its name (can prefix `optional:`), use default if null
     */
    @Nullable
    protected Map<String, Resource> attachment = null;
    /**
     * Whether to send html mail (text/html), otherwise text mail(text/plain).
     * use default if null
     */
    protected Boolean html;
    /**
     * Business keyword ot mark, space seperated, use default if null
     */
    protected String mark;

    /**
     * Schedule to send mail (system time zone)
     */
    protected LocalDateTime date;

    /**
     * Max count of fail, defaults to system configuration
     */
    protected Integer maxFail = 0;
    /**
     * Max count of done (successfully send), defaults to system configuration
     */
    protected Integer maxDone = 0;

    /**
     * Reference Type to Tag key1, key2 usage
     */
    private Integer refType;

    /**
     * Reference key1, Generally the PK
     */
    private Long refKey1;

    /**
     * Reference key2, Generally the composite type
     */
    private String refKey2;

    //

    public void setContentText(String content) {
        this.content = content;
        this.html = false;
    }

    public void setContentHtml(String content) {
        this.content = content;
        this.html = true;
    }

    public void setContentHtml(String content, Boolean html) {
        this.content = content;
        this.html = html;
    }

    public void putAttachment(@NotNull String name, @NotNull Resource resource, boolean optional) {
        if (attachment == null) attachment = new LinkedHashMap<>();
        if (optional) name = PropHelper.prefixOptional(name);

        attachment.put(name, resource);
    }
}

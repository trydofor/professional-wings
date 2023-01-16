package pro.fessional.wings.tiny.mail.service;

import lombok.Data;
import org.springframework.core.io.Resource;

import java.util.Map;

/**
 * @author trydofor
 * @since 2023-01-12
 */
@Data
public class TinyMail {
    /**
     * 配置的名字，空时使用默认值
     */
    protected String conf;
    /**
     * 默认发件人，空时使用默认值
     */
    protected String from;
    /**
     * 默认收件人，仅null时使用默认值
     */
    protected String[] to;

    public void setTo(String... to) {
        this.to = to;
    }

    /**
     * 默认抄送，仅null时使用默认值
     */
    protected String[] cc;

    public void setCc(String... cc) {
        this.cc = cc;
    }

    /**
     * 默认暗送，仅null时使用默认值
     */
    protected String[] bcc;

    public void setBcc(String... bcc) {
        this.bcc = bcc;
    }

    /**
     * 默认回复，空时使用默认值
     */
    protected String reply;
    /**
     * 默认是否发送html邮件(text/html)，否则纯文本(text/plain)，仅null时使用默认值
     */
    protected Boolean html;
    /**
     * 邮件标题，空时使用默认值
     */
    protected String subject;
    /**
     * 邮件正文，空时使用默认值
     */
    protected String content;
    /**
     * 邮件附件，仅null时使用默认值
     */
    protected Map<String, Resource> attachment = null;
    /**
     * 标记关键词，空格分隔业务key，仅null时使用默认值
     */
    protected String mark;

    /**
     * 最大失败次数，默认为系统配置
     */
    protected int maxFail = 0;
    /**
     * 最大成功次数，默认为系统配置
     */
    protected int maxDone = 0;

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
}

package pro.fessional.wings.tiny.mail.service;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;

import java.util.Map;

/**
 * 可选择同步或异步发送邮件，首先存入database，保证邮件一定发送。
 *
 * @author trydofor
 * @since 2022-12-29
 */
public interface TinyMailService {

    /**
     * 同步发送，发送成功或失败，或异常。失败时，可进行异步retry
     */
    boolean send(@NotNull TinyMail message, boolean retry);

    /**
     * 同步发送 fire and forget，不会抛出异常。失败时，可进行异步retry
     */
    boolean post(@NotNull TinyMail message, boolean retry);

    /**
     * 异步发送，忽略异常，自动进行批量处理。失败时，可进行异步retry
     */
    void emit(@NotNull TinyMail message, boolean retry);

    /**
     * 同步发送，发送成功或失败，或异常。失败时，可进行异步retry
     */
    boolean send(long id, boolean retry);

    /**
     * 同步发送 fire and forget，不会抛出异常。失败时，可进行异步retry
     */
    boolean post(long id, boolean retry);

    /**
     * 异步发送，忽略异常，自动进行批量处理。失败时，可进行异步retry
     */
    void emit(long id, boolean retry);

    @Data
    class TinyMail {
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
        /**
         * 默认抄送，仅null时使用默认值
         */
        protected String[] cc;
        /**
         * 默认暗送，仅null时使用默认值
         */
        protected String[] bcc;
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
        protected String context;
        /**
         * 邮件附件，仅null时使用默认值
         */
        protected Map<String, Resource> attachment = null;
        /**
         * 标记关键词，空格分隔业务key，仅null时使用默认值
         */
        protected String mark;
    }
}

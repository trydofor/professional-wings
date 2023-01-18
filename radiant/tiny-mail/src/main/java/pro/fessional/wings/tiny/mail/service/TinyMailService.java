package pro.fessional.wings.tiny.mail.service;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.cast.BoxedCastUtil;
import pro.fessional.mirana.time.ThreadNow;

import java.time.LocalDateTime;

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
     * 异步发送，忽略异常，自动进行批量处理。失败时，可进行异步retry，返回预计发送时间，-1为失败
     */
    long emit(@NotNull TinyMail message, boolean retry);

    /**
     * 同步发送，发送成功或失败，或异常。失败时，可进行异步retry
     */
    default boolean send(@NotNull TinyMailPlain message) {
        final long id = save(message);
        return send(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
    }

    /**
     * 同步发送 fire and forget，不会抛出异常。失败时，可进行异步retry
     */
    default boolean post(@NotNull TinyMailPlain message) {
        final long id = save(message);
        return post(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
    }

    /**
     * 异步发送，忽略异常，自动进行批量处理。失败时，可进行异步retry，返回预计发送时间，-1为失败
     */
    default long emit(@NotNull TinyMailPlain message) {
        final long id = save(message);
        return emit(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
    }

    /**
     * 同步发送，发送成功或失败，或异常。失败时，可进行异步retry，发送前是否check状态
     */
    boolean send(long id, boolean retry, boolean check);

    /**
     * 同步发送 fire and forget，不会抛出异常。失败时，可进行异步retry，发送前是否check状态
     */
    boolean post(long id, boolean retry, boolean check);

    /**
     * 异步发送，忽略异常，自动进行批量处理。失败时，可进行异步retry，发送前是否check状态，返回预计发送时间，-1为失败
     */
    long emit(long id, boolean retry, boolean check);

    /**
     * 新建(id为空)或编辑一个邮件，返回id
     */
    long save(@NotNull TinyMailPlain message);

    /**
     * 同步扫描补发的邮件，返回补发的件数
     */
    int scan();


    /**
     * 自动发送，根据时间自行决定同步还是异步发送，-1为发送失败，0为同步发送，否则为异步发送时间
     */
    default long auto(@NotNull TinyMail message, boolean retry) {
        final LocalDateTime md = message.getDate();
        if (md == null || md.isBefore(ThreadNow.localDateTime())) {
            final boolean ok = send(message, retry);
            return ok ? 0 : -1;
        }
        else {
            return emit(message, retry);
        }
    }

    /**
     * 自动发送，根据时间自行决定同步还是异步发送，-1为发送失败，0为同步发送，否则为异步发送时间
     */
    default long auto(@NotNull TinyMailPlain message) {
        final long id = save(message);
        final LocalDateTime md = message.getDate();
        if (md == null || md.isBefore(ThreadNow.localDateTime())) {
            final boolean ok = send(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
            return ok ? 0 : -1;
        }
        else {
            return emit(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
        }
    }
}

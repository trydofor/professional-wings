package pro.fessional.wings.tiny.mail.service;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.cast.BoxedCastUtil;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.tiny.mail.database.autogen.tables.pojos.WinMailSender;

import java.time.LocalDateTime;

/**
 * Send mail sync or async, save to database first to ensure mail must be sent.
 *
 * @author trydofor
 * @since 2022-12-29
 */
public interface TinyMailService {

    /**
     * Sync send, return success or not, or throw exception.
     * If not success, async retry
     */
    boolean send(@NotNull TinyMail message, boolean retry);

    /**
     * Sync send, fire and forget, no exception throw.
     * If not success, async retry
     */
    boolean post(@NotNull TinyMail message, boolean retry);

    /**
     * Async, no exception throw. auto batch send.
     * Return the estimated sending time, `-1` for failure
     * If not success, async retry.
     */
    long emit(@NotNull TinyMail message, boolean retry);

    /**
     * Sync send, return success or not, or throw exception.
     * If not success, async retry
     */
    default boolean send(@NotNull TinyMailPlain message) {
        final long id = save(message);
        return send(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
    }

    /**
     * Sync send, fire and forget, no exception throw.
     * If not success, async retry
     */
    default boolean post(@NotNull TinyMailPlain message) {
        final long id = save(message);
        return post(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
    }

    /**
     * Async, no exception throw. auto batch send.
     * Return the estimated sending time, `-1` for failure
     * If not success, async retry.
     */
    default long emit(@NotNull TinyMailPlain message) {
        final long id = save(message);
        return emit(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
    }

    /**
     * Sync send, fire and forget, no exception throw.
     * If not success, async retry, whether to check state before sending
     */
    boolean send(long id, boolean retry, boolean check);

    /**
     * Sync send, fire and forget, no exception throw.
     * If not success, async retry, whether to check state before sending
     */
    boolean post(long id, boolean retry, boolean check);

    /**
     * Async, no exception throw. auto batch send.
     * Return the estimated sending time, `-1` for failure
     * If not success, async retry, whether to check state before sending
     */
    long emit(long id, boolean retry, boolean check);

    /**
     * Create(id is empty) or edit a mail, return the id
     */
    long save(@NotNull TinyMailPlain message);

    /**
     * Sync scan the mail to resend, return the count, and send them async
     */
    int scan();


    /**
     * Create the mail, and auto send it in sync or async way.
     * `-1` means failure, `0` means sync send,
     * otherwise means async send at estimated sending time
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
     * Create the mail, and auto send it in sync or async way.
     * `-1` means failure, `0` means sync send,
     * otherwise means async send at estimated sending time
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

    /**
     * hook the sending result (success/failure), and can stop the next send.
     * should not throw exception in the hook.
     */
    interface StatusHook {
        /**
         * hook status, return true will stop mail next send
         *
         * @param po        mail info
         * @param cost      send cost
         * @param exception error if fail
         * @return whether stop next send
         */
        boolean stop(@NotNull WinMailSender po, long cost, Exception exception);
    }
}

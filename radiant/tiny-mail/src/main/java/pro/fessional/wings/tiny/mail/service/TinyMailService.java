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
     * <pre>
     * Save and Sync single send. and return,
     * - false, if check fail.
     * - throw if send fail, MailRetryException if async retry.
     * - true, otherwise.
     * </pre>
     */
    boolean send(@NotNull TinyMail message, boolean retry);

    /**
     * <pre>
     * Save and Sync single send, fire and forget, no exception throw. and return,
     * - -2, if throw non MailRetryException.
     * - -1, if check fail.
     * - 0, if send success.
     * - &gt; now(), (estimated retry time) if fail and async retry
     * </pre>
     */
    long post(@NotNull TinyMail message, boolean retry);

    /**
     * <pre>
     * Async batch send, no exception throw. auto in batch send. and return,
     * - -2, if throw non MailRetryException.
     * - -1 if check fail.
     * - &gt; now() estimated retry time if fail and async retry
     * </pre>
     */
    long emit(@NotNull TinyMail message, boolean retry);

    /**
     * <pre>
     * Save and Sync single send. and return,
     * - false, if check fail.
     * - throw if send fail, MailRetryException if async retry.
     * - true, otherwise.
     * </pre>
     */
    default boolean send(@NotNull TinyMailPlain message) {
        final long id = save(message);
        return send(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
    }

    /**
     * <pre>
     * Save and Sync single send, fire and forget, no exception throw. and return,
     * - -1, if check fail.
     * - 0, if send success.
     * - &gt; now(), (estimated retry time) if fail and async retry
     * </pre>
     */
    default long post(@NotNull TinyMailPlain message) {
        final long id = save(message);
        return post(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
    }

    /**
     * <pre>
     * Save and Async batch send, no exception throw. auto in batch send. and return,
     * - -1 if check fail.
     * - &gt; now() estimated retry time if fail and async retry
     * </pre>
     */
    default long emit(@NotNull TinyMailPlain message) {
        final long id = save(message);
        return emit(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
    }

    /**
     * <pre>
     * Load and Sync single send. and return,
     * - false, if check fail.
     * - throw if send fail, MailRetryException if async retry.
     * - true, otherwise.
     * </pre>
     */
    boolean send(long id, boolean retry, boolean check);

    /**
     * <pre>
     * Load and Sync single send, fire and forget, no exception throw. and return,
     * - -1, if check fail.
     * - 0, if send success.
     * - &gt; now(), (estimated retry time) if fail and async retry
     * </pre>
     */
    long post(long id, boolean retry, boolean check);

    /**
     * <pre>
     * Load and Async batch send, no exception throw. auto in batch send. and return,
     * - -1 if check fail.
     * - &gt; now() estimated retry time if fail and async retry
     * </pre>
     */
    long emit(long id, boolean retry, boolean check);

    /**
     * Create(id is empty) or edit a mail, return the id.
     * NOTE: no schedule to send, need manually send/post/emit it.
     */
    long save(@NotNull TinyMailPlain message);

    /**
     * <pre>
     * Sync scan the unsent mail to resend them async, return the count. and if idel is
     * * null, only scan, nothing to idle
     * * &gt; 0, adjust the scheduled scan interval mills
     * * = 0, disable the scheduled scan
     * * &lt; 0, reset to scan-idle prop if adjusted before
     * </pre>
     */
    int scan(Long idle);

    /**
     * Sync scan the unsent mail to resend them async, return the count.
     */
    default int scan() {
        return scan(null);
    }

    /**
     * <pre>
     * Save and auto post/emit by its mail-date. and retrun,
     * - -1, if check fail.
     * - 0, if send success.
     * - &gt; now(), (estimated retry time) if fail and async retry
     * </pre>
     */
    default long auto(@NotNull TinyMail message, boolean retry) {
        final LocalDateTime md = message.getDate();
        if (md == null || md.isBefore(ThreadNow.localDateTime())) {
            return post(message, retry);
        }
        else {
            return emit(message, retry);
        }
    }

    /**
     * <pre>
     * Save and auto post/emit by its mail-date. and retrun,
     * - -1, if check fail.
     * - 0, if send success.
     * - &gt; now(), (estimated retry time) if fail and async retry
     * </pre>
     */
    default long auto(@NotNull TinyMailPlain message) {
        final long id = save(message);
        final LocalDateTime md = message.getDate();
        if (md == null || md.isBefore(ThreadNow.localDateTime())) {
            return post(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
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

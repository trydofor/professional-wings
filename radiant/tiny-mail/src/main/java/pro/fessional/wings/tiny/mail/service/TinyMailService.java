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
     * send success
     */
    int Success = 0;

    /**
     * failed to check before send. e.g. prop, lock or format
     */
    int ErrCheck = -1;

    /**
     * other than MailRetryException after check
     */
    int ErrOther = -2;

    /**
     * <pre>
     * Save first, then Sync single send. and return,
     * - true, if send success.
     * - false, if check fail, e.g. prop, lock or format.
     * - throw if send fail, MailRetryException if async retry.
     * </pre>
     *
     * @throws MailRetryException if retry
     * @throws Exception          if unhandled
     */
    boolean send(@NotNull TinyMail message, boolean retry);

    /**
     * <pre>
     * Save first, then Sync single send, fire and forget, no exception throw. and return,
     * - {@value #ErrOther}, if throw non MailRetryException.
     * - {@value #ErrCheck}, if check fail, e.g. prop, lock or format.
     * - {@value #Success}, if send success.
     * - mills &gt; now(), estimated retry time, if fail and async retry
     * </pre>
     */
    long post(@NotNull TinyMail message, boolean retry);

    /**
     * <pre>
     * Save first, then Async batch send, fire and forget, no exception throw. and return,
     * - {@value #ErrOther}, if throw non MailRetryException.
     * - {@value #ErrCheck}, if check fail, e.g. prop, lock or format.
     * - mills &gt; now(), estimated send or retry (when error) time
     * </pre>
     */
    long emit(@NotNull TinyMail message, boolean retry);

    /**
     * <pre>
     * Save first, then Sync single send. and return,
     * - true, if send success.
     * - false, if check fail, e.g. prop, lock or format.
     * - throw if send fail, MailRetryException if async retry.
     * </pre>
     *
     * @throws MailRetryException if retry
     * @throws Exception          if unhandled
     */
    default boolean send(@NotNull TinyMailPlain message) {
        final long id = save(message);
        return send(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
    }

    /**
     * <pre>
     * Save first, then Sync single send, fire and forget, no exception throw. and return,
     * - {@value #ErrOther}, if throw non MailRetryException.
     * - {@value #ErrCheck}, if check fail, e.g. prop, lock or format.
     * - {@value #Success}, if send success.
     * - mills &gt; now(), estimated retry time, if fail and async retry
     * </pre>
     */
    default long post(@NotNull TinyMailPlain message) {
        final long id = save(message);
        return post(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
    }

    /**
     * <pre>
     * Save first, then Async batch send, fire and forget, no exception throw. and return,
     * - {@value #ErrOther}, if throw non MailRetryException.
     * - {@value #ErrCheck}, if check fail, e.g. prop, lock or format.
     * - mills &gt; now(), estimated send or retry (when error) time
     * </pre>
     */
    default long emit(@NotNull TinyMailPlain message) {
        final long id = save(message);
        return emit(id, BoxedCastUtil.orFalse(message.getRetry()), BoxedCastUtil.orFalse(message.getCheck()));
    }

    /**
     * <pre>
     * Save first, then Sync single send. and return,
     * - true, if send success.
     * - false, if check fail, e.g. prop, lock or format.
     * - throw if send fail, MailRetryException if async retry.
     * </pre>
     *
     * @throws MailRetryException if retry
     * @throws Exception          if unhandled
     */
    boolean send(long id, boolean retry, boolean check);

    /**
     * <pre>
     * Save first, then Sync single send, fire and forget, no exception throw. and return,
     * - {@value #ErrOther}, if throw non MailRetryException.
     * - {@value #ErrCheck}, if check fail, e.g. prop, lock or format.
     * - {@value #Success}, if send success.
     * - mills &gt; now(), estimated retry time, if fail and async retry
     * </pre>
     */
    long post(long id, boolean retry, boolean check);

    /**
     * <pre>
     * Save first, then Async batch send, fire and forget, no exception throw. and return,
     * - {@value #ErrOther}, if throw non MailRetryException.
     * - {@value #ErrCheck}, if check fail, e.g. prop, lock or format.
     * - mills &gt; now(), estimated send or retry (when error) time
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
     * Save first, then auto post/emit by its mail-date. and retrun,
     * - {@value #ErrCheck}, if check fail, e.g. prop, lock or format.
     * - {@value #Success}, if send success.
     * - mills &gt; now(), estimated retry time, if fail and async retry
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
     * Save first, then auto post/emit by its mail-date. and retrun,
     * - {@value #ErrCheck}, if check fail, e.g. prop, lock or format.
     * - {@value #Success}, if send success.
     * - mills &gt; now(), estimated retry time, if fail and async retry
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

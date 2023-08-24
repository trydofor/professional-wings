package pro.fessional.wings.tiny.mail.sender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.faceless.convention.EmptySugar;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailEnabledProp;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailSenderProp;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Support dryrun, replacement and DayLimitException
 *
 * @author trydofor
 * @since 2023-01-03
 */
@Slf4j
@RequiredArgsConstructor
public class MailSenderManager {

    @Getter
    protected final TinyMailSenderProp senderProp;
    @Getter
    protected final MailSenderProvider senderProvider;
    @Getter
    @Setter(onMethod_ = {@Value("${" + TinyMailEnabledProp.Key$dryrun + "}")})
    protected boolean dryrun = false;

    protected final ConcurrentHashMap<String, Long> mailHostWait = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, Long> mailHostIdle = new ConcurrentHashMap<>();

    /**
     * Remove host from waiting
     */
    public void removeHostWait(String host) {
        mailHostWait.remove(host);
    }

    /**
     * Remove cached sender by its config.name
     *
     * @param name config.name
     */
    public void removeCachingSender(String name) {
        senderProvider.removeCaching(name);
    }

    /**
     * List all hosts waiting for frequency limit
     */
    public Map<String, Long> listHostWait() {
        return new HashMap<>(mailHostWait);
    }


    public void singleSend(@NotNull TinyMailMessage message) {
        singleSend(message, 0, null);
    }

    @SneakyThrows
    public void checkMessage(@NotNull TinyMailMessage message) {
        final JavaMailSender sender = senderProvider.singletonSender(message);
        prepareMimeMessage(message, null, sender);
    }

    public void singleSend(@NotNull TinyMailMessage message, long maxWait) {
        singleSend(message, maxWait, null);
    }

    public void singleSend(@NotNull TinyMailMessage message, @Nullable MimeMessagePrepareHelper preparer) {
        singleSend(message, 0, preparer);
    }

    /**
     * Supports dryrun, send one mail per connect login, MailException is wrapped to MailWaitException
     *
     * @throws MailWaitException need handle the waitMillis and non-null cause (original exception)
     */
    @SneakyThrows
    public void singleSend(@NotNull TinyMailMessage message, long maxWait, @Nullable MimeMessagePrepareHelper preparer) {
        if (dryrun) {
            final int slp = RandomUtils.nextInt(10, 2000);
            Sleep.ignoreInterrupt(slp);
            log.info("single mail dryrun and sleep {} ms", slp);
            return;
        }

        final String host = message.getHost();
        checkHostWaitOrIdle(host, maxWait);

        final JavaMailSender sender = senderProvider.singletonSender(message);
        final MimeMessage mimeMessage = prepareMimeMessage(message, preparer, sender);
        long now = -1;
        try {
            sender.send(mimeMessage);
        }
        catch (Exception me) {
            now = ThreadNow.millis();
            final Wait wt = dealErrorWait(me, now);
            if (wt != null) {
                if (wt.host) {
                    mailHostWait.put(host, wt.wait);
                }
                log.warn("failed to send and host wait for " + (wt.wait - now) + "ms, message=" + message.toMainString(), me);
                throw new MailWaitException(wt.wait, wt.host, wt.stop, me);
            }
            log.warn("failed to send message= " + message.toMainString(), me);
            throw me;
        }
        finally {
            treatHostIdle(host, now);
        }
    }

    public List<BatchResult> batchSend(Collection<? extends TinyMailMessage> messages) {
        return batchSend(messages, 0, null);
    }

    public List<BatchResult> batchSend(Collection<? extends TinyMailMessage> messages, long maxWait) {
        return batchSend(messages, maxWait, null);
    }

    public List<BatchResult> batchSend(Collection<? extends TinyMailMessage> messages, @Nullable MimeMessagePrepareHelper preparer) {
        return batchSend(messages, 0, preparer);
    }

    /**
     * Supports dryrun, batch send mails per connect login, need to handle the batch result
     */
    public List<BatchResult> batchSend(Collection<? extends TinyMailMessage> messages, long maxWait, @Nullable MimeMessagePrepareHelper preparer) {
        if (messages.isEmpty()) return Collections.emptyList();

        final List<BatchResult> results = new ArrayList<>(messages.size());
        if (dryrun) {
            final int slp = RandomUtils.nextInt(10, 2000);
            Sleep.ignoreInterrupt(slp);
            log.info("batch mail dryrun and sleep {} ms", slp);
            final long avg = slp / messages.size();
            for (TinyMailMessage message : messages) {
                final BatchResult br = new BatchResult();
                br.tinyMessage = message;
                br.costMillis = avg;
                results.add(br);
            }
            return results;
        }

        for (String host : messages.stream().map(MailProperties::getHost).collect(Collectors.toSet())) {
            checkHostWaitOrIdle(host, maxWait);
        }

        final HashMap<JavaMailSender, ArrayList<BatchResult>> senderGroup = new HashMap<>();
        for (TinyMailMessage message : messages) {
            final BatchResult temp = new BatchResult();
            temp.tinyMessage = message;
            try {
                temp.mailSender = senderProvider.singletonSender(message);
                temp.mimeMessage = prepareMimeMessage(message, preparer, temp.mailSender);
            }
            catch (Exception e) {
                temp.exception = e;
            }

            results.add(temp);
            if (temp.mimeMessage != null) {
                senderGroup.computeIfAbsent(temp.mailSender, ignored -> new ArrayList<>())
                           .add(temp);
            }
        }

        for (Map.Entry<JavaMailSender, ArrayList<BatchResult>> en : senderGroup.entrySet()) {
            final JavaMailSender sender = en.getKey();
            final List<BatchResult> result = en.getValue();
            if (result.isEmpty()) continue;

            final int len = result.size();
            final MimeMessage[] mineMessages = new MimeMessage[len];
            final HashSet<String> hosts = new HashSet<>();
            for (int i = 0; i < len; i++) {
                final BatchResult temp = result.get(i);
                mineMessages[i] = temp.mimeMessage;
                hosts.add(temp.tinyMessage.getHost());
            }

            long start = ThreadNow.millis();
            long now = -1;
            try {
                sender.send(mineMessages);
            }
            catch (Exception me) {
                now = ThreadNow.millis();
                final Wait wt = dealErrorWait(me, now);
                if (wt != null) {
                    // group by sender. one sender should have only one host
                    for (String host : hosts) {
                        mailHostWait.put(host, wt.wait);
                    }
                    log.warn("failed to send and host wait for " + (wt.wait - now) + "ms, hosts=" + hosts, me);
                }

                if (me instanceof MailSendException) {
                    final Map<Object, Exception> fms = ((MailSendException) me).getFailedMessages();
                    for (BatchResult br : result) {
                        final Exception ex = fms.get(br.mimeMessage);
                        if (ex != null) {
                            br.exception = wt != null ? new MailWaitException(wt.wait, wt.host, wt.stop, ex) : ex;
                        }
                    }
                }
                else {
                    final Exception mw = wt != null ? new MailWaitException(wt.wait, wt.host, wt.stop, me) : me;
                    for (BatchResult br : result) {
                        br.exception = mw;
                    }
                }
            }
            finally {
                if (now < 0) now = ThreadNow.millis();

                long avg = (now - start) / len;
                for (BatchResult br : result) {
                    br.costMillis = avg;
                    br.doneMillis = now;
                    if (br.exception != null) {
                        log.warn("failed to batch send message, " + br.tinyMessage.toMainString());
                    }
                }

                for (String host : hosts) {
                    treatHostIdle(host, now);
                }
            }
        }

        return results;
    }

    private void treatHostIdle(String host, long now) {
        final long idle = senderProp.getPerIdle().getOrDefault(host, Duration.ZERO).toMillis();
        if (idle > 0) {
            if (now < 0) now = ThreadNow.millis();
            mailHostIdle.put(host, now + idle);
        }
    }

    private void checkHostWaitOrIdle(String host, long maxWait) {
        final Long wait = mailHostWait.get(host);
        long now = -1;
        if (wait != null) {
            now = ThreadNow.millis();
            if (wait > now) {
                log.warn("mail need wait {}ms, host={}", host, wait - now);
                throw new MailWaitException(wait, true, false, null);
            }
            else {
                mailHostWait.remove(host);
            }
        }

        final long perIdle = senderProp.getPerIdle().getOrDefault(host, Duration.ZERO).toMillis();
        if (perIdle > 0) {
            final Long idle = mailHostIdle.get(host);
            if (idle != null && idle > 0) {
                if (now < 0) now = ThreadNow.millis();
                if (idle > now) {
                    final long maxIdle = maxWait > 0 ? maxWait : senderProp.getMaxIdle().getOrDefault(host, Duration.ZERO).toMillis();
                    final long tm = idle - now;
                    log.warn("mail need idle {}ms, host={} ", host, tm);
                    if (maxIdle > 0 && idle > now + maxIdle) {
                        throw new MailWaitException(idle, true, false, null);
                    }
                    else {
                        Sleep.ignoreInterrupt(tm);
                    }
                }
                mailHostIdle.put(host, 0L);
            }
        }
    }

    private MimeMessage prepareMimeMessage(TinyMailMessage message, MimeMessagePrepareHelper preparer, JavaMailSender sender) throws MessagingException {

        if (sender == null) {
            sender = senderProvider.singletonSender(message);
        }

        final MimeMessage mineMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mineMessage, true);

        final String[] fto = senderProp.getForceTo();
        if (fto != null && fto.length > 0) {
            helper.setTo(fto);
        }
        else {
            final String[] to = message.getTo();
            helper.setTo(to);
        }

        final String from = message.getFrom();
        if (isNotEmpty(from)) {
            helper.setFrom(from);
        }

        final String[] fcc = senderProp.getForceCc();
        if (fcc != null && fcc.length > 0) {
            helper.setCc(fcc);
        }
        else {
            final String[] cc = message.getCc();
            if (cc != null) {
                helper.setCc(cc);
            }
        }

        final String[] fbcc = senderProp.getForceBcc();
        if (fbcc != null && fbcc.length > 0) {
            helper.setBcc(fbcc);
        }
        else {
            final String[] bcc = message.getBcc();
            if (bcc != null) {
                helper.setBcc(bcc);
            }
        }

        final String reply = message.getReply();
        if (isNotEmpty(reply)) {
            helper.setReplyTo(reply);
        }

        final Map<String, Resource> files = message.getAttachment();
        if (files != null) {
            for (Map.Entry<String, Resource> en : files.entrySet()) {
                helper.addAttachment(en.getKey(), en.getValue());
            }
        }

        final String fpre = senderProp.getForcePrefix();
        final String subject = EmptySugar.nullToEmpty(message.getSubject());
        if (fpre != null && !fpre.isEmpty()) {
            helper.setSubject(fpre + " " + subject);
        }
        else {
            helper.setSubject(subject);
        }

        helper.setText(EmptySugar.nullToEmpty(message.getContent()), message.asHtml());

        final String bizId = senderProp.getBizId();
        if (isNotEmpty(bizId) && message.getBizId() != null) {
            mineMessage.addHeader(bizId, String.valueOf(message.getBizId()));
        }

        final String bizMark = senderProp.getBizMark();
        if (isNotEmpty(bizMark) && isNotEmpty(message.getBizMark())) {
            mineMessage.addHeader(bizMark, message.getBizMark());
        }

        if (preparer != null) {
            preparer.prepare(message, helper);
        }

        return mineMessage;
    }

    private Wait dealErrorWait(Exception me, long now) {
        final String msg = me.getMessage();
        if (isNotEmpty(msg)) {
            for (Map.Entry<BigDecimal, String> en : senderProp.getErrHost().entrySet()) {
                if (msg.contains(en.getValue())) {
                    return Wait.host(now, en.getKey().longValue() * 1000);
                }
            }

            for (Map.Entry<BigDecimal, String> en : senderProp.getErrMail().entrySet()) {
                if (msg.contains(en.getValue())) {
                    return Wait.mail(now, en.getKey().longValue() * 1000);
                }
            }
        }

        if (me instanceof MailAuthenticationException) {
            final Duration dur = senderProp.getErrAuth();
            if (dur != null) {
                return Wait.mail(now, dur.toMillis());
            }
        }

        if (me instanceof MailSendException) {
            final Duration dur = senderProp.getErrSend();
            if (dur != null) {
                return Wait.mail(now, dur.toMillis());
            }
        }

        return null;
    }

    private static class Wait {
        private final long wait;
        private final boolean host;
        private final boolean stop;

        public Wait(long wait, boolean host, boolean stop) {
            this.wait = wait;
            this.host = host;
            this.stop = stop;
        }

        private static Wait host(long now, long dur) {
            return new Wait(now + Math.abs(dur), true, dur < 0);
        }

        private static Wait mail(long now, long dur) {
            return new Wait(now + Math.abs(dur), false, dur < 0);
        }
    }

    public static class BatchResult {
        @Getter
        private TinyMailMessage tinyMessage;
        @Getter
        private long costMillis = 0;
        @Getter
        private long doneMillis = 0;
        @Getter
        private Exception exception;

        private JavaMailSender mailSender;
        private MimeMessage mimeMessage;
    }

    public interface MimeMessagePrepareHelper {
        void prepare(TinyMailConfig config, MimeMessageHelper helper) throws MessagingException;
    }
}

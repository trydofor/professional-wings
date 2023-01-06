package pro.fessional.wings.tiny.mail.sender;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
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

import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * 支持force替换，DayLimitException
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
     * 清除host的等待
     */
    public void cleanHostWait(String host) {
        mailHostWait.remove(host);
    }

    /**
     * 列出当前等待的host
     */
    public Map<String, Long> listHostWait() {
        return new HashMap<>(mailHostWait);
    }


    public void singleSend(@NotNull TinyMailMessage message) {
        singleSend(message, null);
    }

    /**
     * 支持dryrun，一次链接验证，发送一个邮件，MailException会被转为MailWaitException
     *
     * @throws MailWaitException 需要处理waitMillis及cause非null时的原始异常
     */
    @SneakyThrows
    public void singleSend(@NotNull TinyMailMessage message, @Nullable MimeMessagePrepareHelper preparer) {
        if (dryrun) {
            final int slp = RandomUtils.nextInt(10, 2000);
            Sleep.ignoreInterrupt(slp);
            log.info("single mail dryrun and sleep {} ms", slp);
            return;
        }

        final JavaMailSender sender = senderProvider.singletonSender(message);
        final MimeMessage mimeMessage = prepareMimeMessage(message, preparer, sender);
        long now = -1;
        try {
            sender.send(mimeMessage);
        }
        catch (Exception me) {
            final long ms = dealErrorWait(me);
            if (ms > 0) {
                now = ThreadNow.millis();
                mailHostWait.put(message.getHost(), now + ms);
                log.warn("failed to send and host wait for " + ms + "ms, message=" + message.toMainString(), me);
                throw new MailWaitException(ms, me);
            }
            log.warn("failed to send message= " + message.toMainString(), me);
            throw me;
        }
        finally {
            final long idle = senderProp.getPerIdle().toMillis();
            if (idle > 0) {
                if (now < 0) now = ThreadNow.millis();
                mailHostIdle.put(message.getHost(), now + idle);
            }
        }
    }

    public List<BatchResult> batchSend(List<TinyMailMessage> messages) {
        return batchSend(messages, null);
    }

    /**
     * 支持dryrun，一次链接验证，发送批量邮件，需要单个处理结果
     */
    public List<BatchResult> batchSend(List<TinyMailMessage> messages, @Nullable MimeMessagePrepareHelper preparer) {
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
                br.millis = avg;
                results.add(br);
            }
            return results;
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
                senderGroup.computeIfAbsent(temp.mailSender, k -> new ArrayList<>())
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
            try {
                sender.send(mineMessages);
            }
            catch (Exception me) {
                final long ms = dealErrorWait(me);
                if (ms > 0) {
                    for (String host : hosts) {
                        mailHostWait.put(host, start + ms);
                    }
                    log.warn("failed to send and host wait for " + ms + "ms, hosts=" + hosts, me);
                }

                if (me instanceof MailSendException) {
                    final Map<Object, Exception> fms = ((MailSendException) me).getFailedMessages();
                    for (BatchResult br : result) {
                        final Exception ex = fms.get(br.mimeMessage);
                        if (ex != null) {
                            br.exception = ms > 0 ? new MailWaitException(ms, ex) : ex;
                        }
                    }
                }
                else {
                    final Exception mw = ms > 0 ? new MailWaitException(ms, me) : me;
                    for (BatchResult br : result) {
                        br.exception = mw;
                    }
                }
            }
            finally {
                final long now = ThreadNow.millis();
                long avg = (now - start) / len;
                for (BatchResult br : result) {
                    br.millis = avg;
                    if (br.exception != null) {
                        log.warn("failed to batch send message=" + br.tinyMessage.toMainString());
                    }
                }
                final long idle = senderProp.getPerIdle().toMillis();
                if (idle > 0) {
                    for (String host : hosts) {
                        mailHostIdle.put(host, now + idle);
                    }
                }
            }
        }

        return results;
    }

    private MimeMessage prepareMimeMessage(TinyMailMessage message, MimeMessagePrepareHelper preparer, JavaMailSender sender) throws Exception {

        final String host = message.getHost();
        final Long wait = mailHostWait.get(host);
        long now = -1;
        if (wait != null) {
            now = ThreadNow.millis();
            if (wait > now) {
                throw new MailWaitException(wait - now, null);
            }
            else {
                mailHostWait.remove(host);
            }
        }

        final long perIdle = senderProp.getPerIdle().toMillis();
        if (perIdle > 0) {
            final Long idle = mailHostIdle.get(host);
            if (idle != null) {
                if (now < 0) now = ThreadNow.millis();
                if (idle > now) {
                    final long maxIdle = senderProp.getMaxIdle().toMillis();
                    if (maxIdle > 0 && idle > now + maxIdle) {
                        throw new MailWaitException(idle - now, null);
                    }
                    else {
                        Sleep.ignoreInterrupt(idle - now);
                    }
                }
            }
        }

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

        final String context = EmptySugar.nullToEmpty(message.getContext());
        helper.setText(context, message.asHtml());

        final String bizId = senderProp.getBizId();
        if (isNotEmpty(bizId) && message.getBizId() != null) {
            mineMessage.addHeader(bizId, String.valueOf(message.getBizId()));
        }

        final String bizMark = senderProp.getBizMark();
        if (isNotEmpty(bizId) && isNotEmpty(message.getBizMark())) {
            mineMessage.addHeader(bizId, message.getBizMark());
        }

        if (preparer != null) {
            preparer.prepare(message, helper);
        }

        return mineMessage;
    }

    private long dealErrorWait(Exception me) {
        final Throwable cause = me.getCause();
        final String msg = cause == null ? me.getMessage() : cause.getMessage();

        if (msg != null) {
            for (Map.Entry<String, Duration> en : senderProp.getErrLike().entrySet()) {
                if (msg.contains(en.getKey())) {
                    return en.getValue().toMillis();
                }
            }
        }

        if (me instanceof MailAuthenticationException) {
            final Duration dur = senderProp.getErrAuth();
            if (dur != null) {
                return dur.toMillis();
            }
        }

        if (me instanceof MailSendException) {
            final Duration dur = senderProp.getErrSend();
            if (dur != null) {
                return dur.toMillis();
            }
        }

        return -1L;
    }

    public static class BatchResult {
        @Getter
        private TinyMailMessage tinyMessage;
        @Getter
        private long millis = 0;
        @Getter
        private Exception exception;

        private JavaMailSender mailSender;
        private MimeMessage mimeMessage;
    }

    public interface MimeMessagePrepareHelper {
        void prepare(TinyMailConfig config, MimeMessageHelper helper) throws Exception;
    }
}

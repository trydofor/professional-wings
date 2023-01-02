package pro.fessional.wings.tiny.mail.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import pro.fessional.mirana.cast.BoxedCastUtil;
import pro.fessional.wings.silencer.notice.SmallNotice;
import pro.fessional.wings.slardar.jackson.AesString;
import pro.fessional.wings.tiny.mail.provider.MailSenderProvider;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailNoticeProp;

import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * @author trydofor
 * @since 2022-12-29
 */
@Slf4j
public class MailNotice implements SmallNotice<MailNotice.Conf> {

    @NotNull
    private final Conf defaultConfig;
    @NotNull
    private final MailSenderProvider senderProvider;
    @NotNull
    private final Executor executor;

    public MailNotice(@NotNull Conf defaultConfig, @NotNull MailSenderProvider senderProvider, @NotNull Executor executor) {
        this.defaultConfig = defaultConfig;
        this.senderProvider = senderProvider;
        this.executor = executor;
    }

    public MailNotice(@NotNull TinyMailNoticeProp configProp, @NotNull MailSenderProvider defaultSender, @NotNull Executor executor) {
        this(configProp.getDefault(), defaultSender, executor);
        this.configs = configProp;
    }

    @Setter @Getter
    private Map<String, Conf> configs = Collections.emptyMap();

    @Override
    @NotNull
    public Conf defaultConfig() {
        return defaultConfig;
    }

    @Override
    @NotNull
    public Conf combineConfig(@NotNull Conf that) {
        Conf conf = new Conf();
        conf.mailFrom = orElse(that.mailFrom, defaultConfig.mailFrom);
        conf.mailTo = orElse(that.mailTo, defaultConfig.mailTo);
        conf.mailCc = orElse(that.mailCc, defaultConfig.mailCc);
        conf.mailBcc = orElse(that.mailBcc, defaultConfig.mailBcc);
        conf.mailReply = orElse(that.mailReply, defaultConfig.mailReply);
        conf.mailHtml = BoxedCastUtil.orElse(that.mailHtml, defaultConfig.mailHtml);
        conf.mailFile = that.mailFile != null ? that.mailFile : defaultConfig.mailFile;
        conf.copy(that);
        return conf;
    }

    @Override
    public Conf provideConfig(@Nullable String name, boolean combine) {
        final Conf conf = configs.get(name);
        if (combine) {
            return conf == null ? defaultConfig : combineConfig(conf);
        }
        else {
            return conf;
        }
    }

    @SneakyThrows
    @Override
    public boolean send(Conf config, String subject, String content) {
        final JavaMailSender sender = senderProvider.cachingSender(config);
        final Map<String, Resource> fileMap = config.mailFile != null ? config.mailFile : Collections.emptyMap();
        final boolean isHtml = config.mailHtml != null ? config.mailHtml : content != null && content.startsWith("<");

        if (isHtml || !fileMap.isEmpty()) {
            final MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(config.mailTo);
            if (StringUtils.isNotEmpty(config.mailFrom)) {
                helper.setFrom(config.mailFrom);
            }
            if (config.mailCc != null) {
                helper.setCc(config.mailCc);
            }
            if (config.mailBcc != null) {
                helper.setBcc(config.mailBcc);
            }
            if (StringUtils.isNotEmpty(config.mailReply)) {
                helper.setReplyTo(config.mailReply);
            }
            if (subject != null) {
                helper.setSubject(subject);
            }
            if (content != null) {
                helper.setText(content, isHtml);
            }
            //
            for (Map.Entry<String, Resource> en : fileMap.entrySet()) {
                helper.addAttachment(en.getKey(), en.getValue());
            }
            sender.send(message);
        }
        else {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(config.mailTo);
            if (StringUtils.isNotEmpty(config.mailFrom)) {
                message.setFrom(config.mailFrom);
            }
            if (config.mailCc != null) {
                message.setCc(config.mailCc);
            }
            if (config.mailBcc != null) {
                message.setBcc(config.mailBcc);
            }
            if (StringUtils.isNotEmpty(config.mailReply)) {
                message.setReplyTo(config.mailReply);
            }
            if (subject != null) {
                message.setSubject(subject);
            }
            if (content != null) {
                message.setText(content);
            }

            sender.send(message);
        }

        return true;
    }


    @Override
    public void emit(Conf config, String subject, String content) {
        executor.execute(() -> send(config, subject, content));
    }

    private String orElse(String conf, String that) {
        return (conf == null || conf.isEmpty() || AesString.MaskedValue.equals(conf)) ? that : conf;
    }

    private String[] orElse(String[] conf, String[] that) {
        return (conf == null || conf.length == 0) ? that : conf;
    }

    /**
     * hashCode和equals回使用MailProperties的host,port,username,protocol
     */
    @Getter
    @Setter
    public static class Conf extends MailProperties {
        private String mailFrom;
        private String[] mailTo;
        private String[] mailCc;
        private String[] mailBcc;
        private String mailReply;
        private Boolean mailHtml;
        private Map<String, Resource> mailFile;

        public void copy(MailProperties prop) {
            setHost(prop.getHost());
            setPort(prop.getPort());
            setUsername(prop.getUsername());
            setPassword(prop.getPassword());
            setProtocol(prop.getProtocol());
            getProperties().putAll(prop.getProperties());
            setJndiName(prop.getJndiName());
            setDefaultEncoding(prop.getDefaultEncoding());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Conf)) return false;
            Conf conf = (Conf) o;
            return mailHtml == conf.mailHtml
                   && Objects.equals(mailFrom, conf.mailFrom)
                   && Arrays.equals(mailTo, conf.mailTo)
                   && Arrays.equals(mailCc, conf.mailCc)
                   && Arrays.equals(mailBcc, conf.mailBcc)
                   && Objects.equals(mailReply, conf.mailReply)
                   && Objects.equals(getHost(), conf.getHost())
                   && Objects.equals(getPort(), conf.getPort())
                   && Objects.equals(getUsername(), conf.getUsername())
                   && Objects.equals(getProtocol(), conf.getProtocol());
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(mailFrom, mailReply, mailHtml
                    , getHost()
                    , getPort()
                    , getUsername()
                    , getProtocol());
            result = 31 * result + Arrays.hashCode(mailTo);
            result = 31 * result + Arrays.hashCode(mailCc);
            result = 31 * result + Arrays.hashCode(mailBcc);
            return result;
        }
    }
}

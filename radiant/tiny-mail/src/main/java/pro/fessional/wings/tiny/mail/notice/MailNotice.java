package pro.fessional.wings.tiny.mail.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import pro.fessional.mirana.cast.BoxedCastUtil;
import pro.fessional.wings.silencer.notice.SmallNotice;
import pro.fessional.wings.slardar.jackson.AesString;
import pro.fessional.wings.tiny.mail.sender.MailSenderProvider;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailConfigProp;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

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

    public MailNotice(@NotNull Conf defaultConfig, @NotNull MailSenderProvider senderProvider) {
        this.defaultConfig = defaultConfig;
        this.senderProvider = senderProvider;
    }

    public MailNotice(@NotNull TinyMailConfigProp configProp, @NotNull MailSenderProvider defaultSender) {
        this(configProp.getDefault(), defaultSender);
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

    @Override
    public boolean send(Conf config, String subject, String content) {
        return false;
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

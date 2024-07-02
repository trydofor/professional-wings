package pro.fessional.wings.tiny.mail.sender;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import pro.fessional.mirana.cond.IfSetter;

import java.util.Arrays;
import java.util.Objects;

import static pro.fessional.wings.silencer.support.PropHelper.invalid;
import static pro.fessional.wings.silencer.support.PropHelper.mergeToInvalid;

/**
 * hashCode and equals with
 * host, port, username, protocol from MailProperties, and
 * TinyMailConfig without name
 *
 * @author trydofor
 * @since 2022-12-31
 */
@Getter
@Setter
public class TinyMailConfig extends MailProperties {

    /**
     * the dryrun prefix of subject. merge if null, `empty` means disable.
     */
    protected String dryrun;
    /**
     * the name of mail config. automatically set in static config, manually set in dynamic config,
     */
    protected String name;
    /**
     * default mail from
     */
    protected String from;
    /**
     * default mail to
     */
    protected String[] to;
    /**
     * default mail cc
     */
    protected String[] cc;
    /**
     * default mail bcc
     */
    protected String[] bcc;
    /**
     * default mail reply
     */
    protected String reply;
    /**
     * mail content type, send html mail(text/html) if true ,otherwise plain mail(text/plain)
     */
    protected Boolean html;

    public void setTo(String... to) {
        this.to = to;
    }

    public void setCc(String... cc) {
        this.cc = cc;
    }

    public void setBcc(String... bcc) {
        this.bcc = bcc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TinyMailConfig conf)) return false;
        return Objects.equals(html, conf.html)
               && Objects.equals(from, conf.from)
               && Objects.equals(reply, conf.reply)
               && Objects.equals(getHost(), conf.getHost())
               && Objects.equals(getPort(), conf.getPort())
               && Objects.equals(getUsername(), conf.getUsername())
               && Objects.equals(getProtocol(), conf.getProtocol())
               && Arrays.equals(to, conf.to)
               && Arrays.equals(cc, conf.cc)
               && Arrays.equals(bcc, conf.bcc);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(
            html,
            from,
            reply
            , getHost()
            , getPort()
            , getUsername()
            , getProtocol());
        result = 31 * result + Arrays.hashCode(to);
        result = 31 * result + Arrays.hashCode(cc);
        result = 31 * result + Arrays.hashCode(bcc);
        return result;
    }

    public static final IfSetter<TinyMailConfig, MailProperties> PropSetter = (thiz, that, absent, present) -> {
        if (that == null) return thiz;

        if (absent == IfSetter.Absent.Invalid) {
            if (invalid(thiz.getHost())) thiz.setHost(that.getHost());
            if (thiz.getPort() == null) thiz.setPort(that.getPort());
            if (invalid(thiz.getUsername())) thiz.setUsername(that.getUsername());
            if (invalid(thiz.getPassword())) thiz.setPassword(that.getPassword());
            if (invalid(thiz.getProtocol())) thiz.setProtocol(that.getProtocol());
            if (thiz.getDefaultEncoding() == null) thiz.setDefaultEncoding(that.getDefaultEncoding());
            mergeToInvalid(thiz.getProperties(), that.getProperties());
        }
        else {
            thiz.setHost(that.getHost());
            thiz.setPort(that.getPort());
            thiz.setUsername(that.getUsername());
            thiz.setPassword(that.getPassword());
            thiz.setProtocol(that.getProtocol());
            thiz.setDefaultEncoding(that.getDefaultEncoding());
            thiz.getProperties().putAll(that.getProperties());
        }

        return thiz;
    };

    public static final IfSetter<TinyMailConfig, TinyMailConfig> ConfSetter = (thiz, that, absent, present) -> {
        if (that == null) return thiz;

        PropSetter.set(thiz, that, absent, present);

        if (absent == IfSetter.Absent.Invalid) {
            if (thiz.dryrun == null) thiz.dryrun = that.dryrun;
            if (invalid(thiz.name)) thiz.name = that.name;
            if (invalid(thiz.from)) thiz.from = that.from;
            if (thiz.to == null) thiz.to = that.to;
            if (thiz.cc == null) thiz.cc = that.cc;
            if (thiz.bcc == null) thiz.bcc = that.bcc;
            if (invalid(thiz.reply)) thiz.reply = that.reply;
            if (thiz.html == null) thiz.html = that.html;
        }
        else {
            thiz.dryrun = that.dryrun;
            thiz.name = that.name;
            thiz.from = that.from;
            thiz.to = that.to;
            thiz.cc = that.cc;
            thiz.bcc = that.bcc;
            thiz.reply = that.reply;
            thiz.html = that.html;
        }

        return thiz;
    };

    public interface Loader {
        /**
         * load config by its name (non-empty)
         */
        @Nullable
        TinyMailConfig load(@NotNull String name);
    }
}

package pro.fessional.wings.tiny.mail.sender;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.autoconfigure.mail.MailProperties;

import java.util.Arrays;
import java.util.Objects;

import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.mergeNotValue;
import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.notValue;

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

    /**
     * use all properties from that
     */
    public void adopt(MailProperties that) {
        if (that == null) return;
        setHost(that.getHost());
        setPort(that.getPort());
        setUsername(that.getUsername());
        setPassword(that.getPassword());
        setProtocol(that.getProtocol());
        setDefaultEncoding(that.getDefaultEncoding());
        getProperties().putAll(that.getProperties());
    }

    /**
     * use all properties from that
     */
    public void adopt(TinyMailConfig that) {
        if (that == null) return;
        adopt((MailProperties) that);
        dryrun = that.dryrun;
        name = that.name;
        from = that.from;
        to = that.to;
        cc = that.cc;
        bcc = that.bcc;
        reply = that.reply;
        html = that.html;
    }

    /**
     * if this.property is invalid, then use that.property.
     * except for 'properties' which merge value only if key matches.
     */
    public void merge(MailProperties that) {
        if (that == null) return;

        if (notValue(getHost())) {
            setHost(that.getHost());
        }
        if (getPort() == null) {
            setPort(that.getPort());
        }
        if (notValue(getUsername())) {
            setUsername(that.getUsername());
        }
        final String password = getPassword();
        if (notValue(password)) {
            setPassword(that.getPassword());
        }
        if (notValue(getProtocol())) {
            setProtocol(that.getProtocol());
        }
        if (getDefaultEncoding() == null) {
            setDefaultEncoding(that.getDefaultEncoding());
        }

        mergeNotValue(getProperties(), that.getProperties());
    }

    /**
     * if this.property is invalid, then use that.property.
     */
    public void merge(TinyMailConfig that) {
        if (that == null) return;
        merge((MailProperties) that);

        if (dryrun == null) dryrun = that.dryrun;
        if (notValue(name)) name = that.name;
        if (notValue(from)) from = that.from;
        if (to == null) to = that.to;
        if (cc == null) cc = that.cc;
        if (bcc == null) bcc = that.bcc;
        if (notValue(reply)) reply = that.reply;
        if (html == null) html = that.html;
    }

    public interface Loader {
        /**
         * load config by its name (non-empty)
         */
        @Nullable
        TinyMailConfig load(@NotNull String name);
    }
}

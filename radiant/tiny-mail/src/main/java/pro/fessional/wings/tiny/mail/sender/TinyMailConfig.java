package pro.fessional.wings.tiny.mail.sender;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.mail.MailProperties;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.notValue;

/**
 * hashCode和equals会使用MailProperties的host,port,username,protocol
 */
@Getter
@Setter
public class TinyMailConfig extends MailProperties {

    /**
     * 配置的名字，自动设置，不纳入hash
     */
    protected String name;
    /**
     * 默认发件人
     */
    protected String from;
    /**
     * 默认收件人
     */
    protected String[] to;
    /**
     * 默认抄送
     */
    protected String[] cc;
    /**
     * 默认暗送
     */
    protected String[] bcc;
    /**
     * 默认回复
     */
    protected String reply;
    /**
     * 默认是否发送html邮件(text/html)，否则纯文本(text/plain)
     */
    protected Boolean html;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TinyMailConfig)) return false;
        TinyMailConfig conf = (TinyMailConfig) o;
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
     * 全部使用that值
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
     * 全部使用that值
     */
    public void adopt(TinyMailConfig that) {
        if (that == null) return;
        adopt((MailProperties) that);

        name = that.name;
        from = that.from;
        to = that.to;
        cc = that.cc;
        bcc = that.bcc;
        reply = that.reply;
        html = that.html;
    }

    /**
     * this值无效时，使用that值
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

        final Map<String, String> thisProp = getProperties();
        final Map<String, String> thatProp = that.getProperties();
        if (!thatProp.isEmpty()) {
            if (thisProp.isEmpty()) {
                thisProp.putAll(thatProp);
            }
            else {
                for (Map.Entry<String, String> en : thisProp.entrySet()) {
                    final String v = en.getValue();
                    if (notValue(v)) {
                        final String tv = thatProp.get(en.getKey());
                        en.setValue(tv);
                    }
                }
            }
        }
    }

    /**
     * this值无效时，使用that值
     */
    public void merge(TinyMailConfig that) {
        if (that == null) return;
        merge((MailProperties) that);

        if (notValue(name)) name = that.name;
        if (notValue(from)) from = that.from;
        if (to == null) to = that.to;
        if (cc == null) cc = that.cc;
        if (bcc == null) bcc = that.bcc;
        if (notValue(reply)) reply = that.reply;
        if (html == null) html = that.html;
    }
}

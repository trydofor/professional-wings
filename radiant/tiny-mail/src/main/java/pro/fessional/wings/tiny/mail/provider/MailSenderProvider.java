package pro.fessional.wings.tiny.mail.provider;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Session;
import javax.naming.NamingException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2022-12-31
 */
public class MailSenderProvider {

    @NotNull
    private final JavaMailSender defaultSender;

    private final ConcurrentHashMap<Key, JavaMailSender> senders = new ConcurrentHashMap<>();

    public MailSenderProvider(@NotNull JavaMailSender defaultSender, @NotNull MailProperties defaultConfig) {
        this.defaultSender = defaultSender;
        this.senders.put(new Key(defaultConfig), defaultSender);
    }

    @NotNull
    public JavaMailSender defaultSender() {
        return defaultSender;
    }

    @NotNull
    public JavaMailSender cachingSender(final @NotNull MailProperties prop) {
        return senders.computeIfAbsent(new Key(prop), k -> newSender(prop));
    }

    @NotNull
    protected JavaMailSender newSender(MailProperties prop) {
        final JavaMailSenderImpl sender = new JavaMailSenderImpl();

        final String jndiName = prop.getJndiName();
        if (StringUtils.isNotEmpty(jndiName)) {
            try {
                final Session session = JndiLocatorDelegate.createDefaultResourceRefLocator().lookup(jndiName, Session.class);
                sender.setDefaultEncoding(prop.getDefaultEncoding().name());
                sender.setSession(session);
            }
            catch (NamingException ex) {
                throw new IllegalStateException("Unable to find Session in JNDI location " + jndiName, ex);
            }
        }
        else {
            sender.setHost(prop.getHost());
            if (prop.getPort() != null) {
                sender.setPort(prop.getPort());
            }
            sender.setUsername(prop.getUsername());
            sender.setPassword(prop.getPassword());
            sender.setProtocol(prop.getProtocol());

            if (prop.getDefaultEncoding() != null) {
                sender.setDefaultEncoding(prop.getDefaultEncoding().name());
            }
            final Map<String, String> mailProp = prop.getProperties();
            if (!mailProp.isEmpty()) {
                Properties pp = new Properties();
                pp.putAll(mailProp);
                sender.setJavaMailProperties(pp);
            }
        }

        return sender;
    }


    protected static class Key {
        private final String host;
        private final Integer port;
        private final String username;
        private final String protocol;
        private final String jndiName;

        public Key(@NotNull MailProperties prop) {
            this.host = prop.getHost();
            this.port = prop.getPort();
            this.username = prop.getUsername();
            this.protocol = prop.getProtocol();
            this.jndiName = prop.getJndiName();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key key = (Key) o;
            return Objects.equals(host, key.host)
                   && Objects.equals(port, key.port)
                   && Objects.equals(username, key.username)
                   && Objects.equals(protocol, key.protocol)
                   && Objects.equals(jndiName, key.jndiName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(host, port, username, protocol, jndiName);
        }
    }
}

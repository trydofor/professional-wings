package pro.fessional.wings.tiny.mail.sender;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Session;
import javax.naming.NamingException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static pro.fessional.wings.tiny.mail.spring.prop.TinyMailConfigProp.KeyDefault;

/**
 * @author trydofor
 * @since 2022-12-31
 */
public class MailSenderProvider {

    @NotNull
    private final JavaMailSender defaultSender;

    private final ConcurrentHashMap<String, JavaMailSender> senders = new ConcurrentHashMap<>();

    public MailSenderProvider(@NotNull JavaMailSender defaultSender) {
        this.defaultSender = defaultSender;
        this.senders.put(KeyDefault, defaultSender);
    }

    @NotNull
    public JavaMailSender defaultSender() {
        return defaultSender;
    }

    @NotNull
    public JavaMailSender singletonSender(@NotNull TinyMailConfig config) {
        return senders.computeIfAbsent(config.getName(), k -> newSender(config));
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

}

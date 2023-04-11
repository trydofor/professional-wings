package pro.fessional.wings.tiny.mail.sender;

import jakarta.mail.Session;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.naming.NamingException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static pro.fessional.wings.tiny.mail.spring.prop.TinyMailConfigProp.KeyDefault;

/**
 * provide sender by config's name
 *
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

    /**
     * get singleton sender by config's name
     */
    @NotNull
    public JavaMailSender singletonSender(@NotNull TinyMailConfig config) {
        final String name = config.getName();
        return name == null || name.isEmpty()
               ? defaultSender
               : senders.computeIfAbsent(name, ignored -> newSender(config));
    }

    public JavaMailSender removeCaching(TinyMailConfig config) {
        if (config == null) return null;
        return senders.remove(config.getName());
    }

    public JavaMailSender removeCaching(String name) {
        if (name == null) return null;
        return senders.remove(name);
    }

    @NotNull
    public JavaMailSender newSender(MailProperties prop) {
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

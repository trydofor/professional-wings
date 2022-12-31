package pro.fessional.wings.tiny.mail.sender;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailConfigProp;

/**
 * @author trydofor
 * @since 2022-12-31
 */
@RequiredArgsConstructor
public class MailSenderProvider {

    @NotNull
    private final JavaMailSender defaultSender;
    @NotNull
    private final TinyMailConfigProp configProp;

    @NotNull
    public JavaMailSender defaultSender() {
        return defaultSender;
    }

    @NotNull
    public JavaMailSender get(@NotNull MailProperties prop) {

        return defaultSender;
    }

}

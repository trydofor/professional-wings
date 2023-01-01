package pro.fessional.wings.tiny.mail.provider;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailNoticeProp;

/**
 * @author trydofor
 * @since 2022-12-31
 */
@RequiredArgsConstructor
public class MailConfigProvider {

    @NotNull
    private final TinyMailNoticeProp configProp;

    @NotNull
    public MailProperties defaultConfig() {
        return configProp.getDefault();
    }

    @Contract("_,true->!null")
    public MailProperties cachingConfig(String name, boolean orDefault) {
        final MailProperties conf = configProp.get(name);
        return conf == null && orDefault ? configProp.getDefault() : conf;
    }
}

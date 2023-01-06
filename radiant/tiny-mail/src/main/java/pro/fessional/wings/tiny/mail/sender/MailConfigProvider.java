package pro.fessional.wings.tiny.mail.sender;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailConfigProp;

/**
 * @author trydofor
 * @since 2022-12-31
 */
@RequiredArgsConstructor
public class MailConfigProvider {

    @NotNull
    private final TinyMailConfigProp configProp;

    @NotNull
    public TinyMailConfig defaultConfig() {
        return configProp.getDefault();
    }

    @Nullable
    public TinyMailConfig bynamedConfig(String name) {
        return configProp.get(name);
    }

    @Contract("_->new")
    public TinyMailConfig combineConfig(@Nullable TinyMailConfig that) {
        final TinyMailConfig newConf = new TinyMailConfig();
        newConf.merge(that);
        newConf.merge(configProp.getDefault());
        return newConf;
    }
}

package pro.fessional.wings.tiny.mail.sender;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailConfigProp;

import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @since 2022-12-31
 */
@RequiredArgsConstructor
public class MailConfigProvider {

    @NotNull
    private final TinyMailConfigProp configProp;

    @Setter(onMethod_ = {@Autowired(required = false)})
    @Getter
    private List<TinyMailConfig.Loader> configLoader = Collections.emptyList();

    @NotNull
    public TinyMailConfig defaultConfig() {
        return configProp.getDefault();
    }

    @Nullable
    public TinyMailConfig bynamedConfig(String name) {
        if (name == null || name.isEmpty()) {
            return defaultConfig();
        }

        TinyMailConfig conf = configProp.get(name);
        if (conf == null && configLoader != null) {
            for (TinyMailConfig.Loader ld : configLoader) {
                final TinyMailConfig cf = ld.load(name);
                if (cf != null) {
                    conf = cf;
                    break;
                }
            }
        }

        return conf;
    }

    @Contract("_->new")
    public TinyMailConfig combineConfig(@Nullable TinyMailConfig that) {
        final TinyMailConfig newConf = new TinyMailConfig();
        newConf.adopt(that);
        newConf.merge(configProp.getDefault());
        return newConf;
    }
}

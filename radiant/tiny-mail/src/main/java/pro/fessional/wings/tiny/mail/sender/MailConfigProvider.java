package pro.fessional.wings.tiny.mail.sender;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.best.ArgsAssert;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailConfigProp;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2022-12-31
 */
@RequiredArgsConstructor
public class MailConfigProvider {

    @NotNull
    private final TinyMailConfigProp configProp;

    private final ConcurrentHashMap<String, TinyMailConfig> dynamicConfig = new ConcurrentHashMap<>();

    @NotNull
    public TinyMailConfig defaultConfig() {
        return configProp.getDefault();
    }

    @Nullable
    public TinyMailConfig bynamedConfig(String name) {
        if (name == null || name.isEmpty()) {
            return defaultConfig();
        }

        final TinyMailConfig conf = dynamicConfig.get(name);
        if (conf != null) {
            return conf;
        }

        return configProp.get(name);
    }

    @Contract("_->new")
    public TinyMailConfig combineConfig(@Nullable TinyMailConfig that) {
        final TinyMailConfig newConf = new TinyMailConfig();
        newConf.adopt(that);
        newConf.merge(configProp.getDefault());
        return newConf;
    }

    /**
     * dynamic put a config, and its name can not be null
     *
     * @param config dynamic config
     */
    public void putMailConfig(@NotNull TinyMailConfig config) {
        final String name = config.getName();
        ArgsAssert.notNull(name, "config.name");


        final TinyMailConfig st = configProp.get(name);
        if (st != null) {
            config.merge(st);
        }

        dynamicConfig.put(name, config);
    }

    /**
     * delete dynamic config by name
     *
     * @param name name
     */
    public void delMailConfig(@NotNull String name) {
        dynamicConfig.remove(name);
    }
}

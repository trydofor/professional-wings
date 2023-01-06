package pro.fessional.wings.tiny.mail.sender;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import pro.fessional.wings.silencer.notice.SmallNotice;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME;

/**
 * @author trydofor
 * @since 2022-12-29
 */
@Slf4j
@RequiredArgsConstructor
public class MailNotice implements SmallNotice<TinyMailConfig> {

    @NotNull @Getter
    protected final MailConfigProvider configProvider;
    @NotNull @Getter
    protected final MailSenderManager senderManager;

    @Setter(onMethod_ = {@Autowired(required = false), @Qualifier(DEFAULT_TASK_SCHEDULER_BEAN_NAME)})
    private Executor executor;

    @Setter @Getter
    private Map<String, TinyMailConfig> configs = Collections.emptyMap();

    @Override
    @NotNull
    public TinyMailConfig defaultConfig() {
        return configProvider.defaultConfig();
    }

    @Override
    public TinyMailConfig combineConfig(@Nullable TinyMailConfig that) {
        return configProvider.combineConfig(that);
    }

    @Override
    public TinyMailConfig provideConfig(@Nullable String name, boolean combine) {
        final TinyMailConfig conf = configProvider.bynamedConfig(name);
        if (combine) {
            return combineConfig(conf == null ? configProvider.defaultConfig() : conf);
        }
        else {
            return conf;
        }
    }

    @SneakyThrows
    @Override
    public boolean send(TinyMailConfig config, String subject, String content) {
        TinyMailMessage message = new TinyMailMessage();
        message.adopt(config);
        message.setSubject(subject);
        message.setContext(content);
        senderManager.singleSend(message);
        return true;
    }

    @Override
    public void emit(TinyMailConfig config, String subject, String content) {
        if (executor == null) {
            log.warn("should reuse autowired thread pool");
            executor = Executors.newSingleThreadExecutor();
        }
        executor.execute(() -> send(config, subject, content));
    }

}

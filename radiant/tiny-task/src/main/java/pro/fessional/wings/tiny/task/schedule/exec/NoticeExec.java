package pro.fessional.wings.tiny.task.schedule.exec;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.AopUtils;
import pro.fessional.wings.silencer.notice.SmallNotice;
import pro.fessional.wings.slardar.jackson.JacksonHelper;
import pro.fessional.wings.tiny.task.schedule.help.TaskerHelper;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2022-12-06
 */
@Getter
@Slf4j
public class NoticeExec<C> {
    @NotNull
    protected final Class<C> confClass;
    @NotNull
    protected final SmallNotice<C> beanObject;
    @NotNull
    protected final Class<?> beanClass;

    @NotNull
    @Setter
    protected Function<C, String> confEncoder = JacksonHelper::string;
    @NotNull
    @Setter
    protected BiFunction<String, Class<C>, C> confDecoder = JacksonHelper::object;

    @SuppressWarnings("unchecked")
    public NoticeExec(@NotNull SmallNotice<C> beanObject) {
        this.confClass = (Class<C>) AopUtils.getTargetClass(beanObject.defaultConfig());
        this.beanClass = AopUtils.getTargetClass(beanObject);
        this.beanObject = beanObject;
    }

    public C decodeConf(String conf) {
        if (conf == null) return null;
        return confDecoder.apply(conf, confClass);
    }

    public String encodeConf(C conf) {
        if (conf == null) return null;
        return confEncoder.apply(conf);
    }

    /**
     * 格式为name:Class，优先匹配name，然后Class
     */
    public boolean accept(String token) {
        return TaskerHelper.acceptToken(beanClass, null, token);
    }

    /**
     * 判断Bean是否一致
     */
    public boolean accept(SmallNotice<C> bean) {
        return TaskerHelper.acceptBean(beanClass, beanObject, bean);
    }

    /**
     * 组合decode的配置，post一个notice
     */
    public void postNotice(String config, String subject, String content) {
        final C tmp = decodeConf(config);
        final C conf = beanObject.combineConfig(tmp);
        beanObject.post(conf, subject, content);
    }
}

package pro.fessional.wings.tiny.task.schedule.exec;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.AopUtils;
import pro.fessional.wings.silencer.notice.SmallNotice;
import pro.fessional.wings.tiny.task.schedule.help.TaskerHelper;

/**
 * @author trydofor
 * @since 2022-12-06
 */
@Getter
@Slf4j
public class NoticeExec<C> {

    public static final String WhenExec = "exec";
    public static final String WhenFail = "fail";
    public static final String WhenDone = "done";
    public static final String WhenFeed = "feed";

    @NotNull
    protected final SmallNotice<C> beanObject;
    @NotNull
    protected final Class<?> beanClass;

    public NoticeExec(@NotNull SmallNotice<C> beanObject) {
        this.beanClass = AopUtils.getTargetClass(beanObject);
        this.beanObject = beanObject;
    }

    /**
     * The format is name:Class, which matches name first, then Class.
     */
    public boolean accept(String token) {
        return TaskerHelper.acceptToken(beanClass, null, token);
    }

    /**
     * Whether the bean is accepted
     */
    public boolean accept(SmallNotice<C> bean) {
        return TaskerHelper.acceptBean(beanClass, beanObject, bean);
    }

    /**
     * Post a notice with combined the config
     */
    public void postNotice(String config, String subject, String content) {
        try {
            final C conf = beanObject.provideConfig(config, true);
            beanObject.post(conf, subject, content);
        }
        catch (Exception e) {
            log.warn("failed to post notice, subject=" + subject + ", content=" + content, e);
        }
    }
}

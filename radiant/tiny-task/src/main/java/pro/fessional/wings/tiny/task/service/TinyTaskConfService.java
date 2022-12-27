package pro.fessional.wings.tiny.task.service;

import lombok.Data;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.data.Diff;
import pro.fessional.wings.tiny.task.schedule.conf.TaskerProp;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * 配置tasker的属性，数据库与配置文件比较，version大的优先，同version时，数据库优先。
 * 此外，提供default配置，非独立属性，可以继承default配置。
 *
 * @author trydofor
 * @since 2022-12-11
 */
public interface TinyTaskConfService {

    /**
     * <pre>
     * 配置TinyTasker标记的指定方法且enabled，并返回taskId
     * 若property不存在，则报错。
     * 若database不存在，则存入。
     * 若database存在，version大的优先，若property大，则存入database，否则无操作。
     * </pre>
     */
    Conf config(@NotNull Object bean, @NotNull Method method, @Nullable Object para);

    /**
     * 配置TinyTasker标记的所有方法，并返回taskId和有效的配置。
     * 若autorun但para不正确的，则报错。
     *
     * @see #config(Object, Method, Object)
     */
    @NotNull
    Set<Conf> config(@NotNull Object bean);

    /**
     * 从数据库获取配置
     */
    @Contract("_,true->!null")
    TaskerProp database(long id, boolean nonnull);

    /**
     * 从配置文件获取配置，根据配置的key获取
     */
    @Contract("_,true->!null")
    TaskerProp property(long id, boolean nonnull);

    @NotNull
    LinkedHashMap<String, Diff.V<?>> diffProp(long id);

    /**
     * 启用或禁用task
     */
    boolean enable(long id, boolean enabled);

    /**
     * 保存配置到数据库
     */
    boolean replace(long id, TaskerProp prop);

    @Data
    class Conf {
        private final long id;
        private final boolean enabled;
        private final boolean autorun;
    }
}

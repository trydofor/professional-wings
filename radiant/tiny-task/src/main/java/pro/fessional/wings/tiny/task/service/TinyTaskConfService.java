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
 * Configure the properties of Tasker, compare the database and configuration files,
 * giving priority to the larger version. When the versions are the same, prioritize the database.
 * Additionally, provide default configurations that can be inherited for non-independent properties.
 *
 * @author trydofor
 * @since 2022-12-11
 */
public interface TinyTaskConfService {

    /**
     * <pre>
     * Configure the TinyTasker annotated and enabled method , return the taskId.
     * - throw exception if property not exist
     * - save to database if not exist in database
     * - save to database, if exist in database, but higher version
     * - otherwise no operation
     * </pre>
     */
    Conf config(@NotNull Object bean, @NotNull Method method, @Nullable Object para);

    /**
     * Configures all TinyTasker annotated methods, and returns the taskId and its config.
     * Throw exception if autorun but para is incorrect.
     *
     * @see #config(Object, Method, Object)
     */
    @NotNull
    Set<Conf> config(@NotNull Object bean);

    /**
     * Load properties from database by taskId
     */
    @Contract("_,true->!null")
    TaskerProp database(long id, boolean nonnull);

    /**
     * Load properties from config file, get value by key
     */
    @Contract("_,true->!null")
    TaskerProp property(long id, boolean nonnull);

    @NotNull
    LinkedHashMap<String, Diff.V<?>> diffProp(long id);

    /**
     * Enable or disable the task
     */
    boolean enable(long id, boolean enabled);

    /**
     * Save properties to database
     */
    boolean replace(long id, TaskerProp prop);

    @Data
    class Conf {
        private final long id;
        private final boolean enabled;
        private final boolean autorun;
    }
}

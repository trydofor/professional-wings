package pro.fessional.wings.tiny.task.service;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.tiny.task.schedule.conf.TaskerProp;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 配置tasker
 *
 * @author trydofor
 * @since 2022-12-11
 */
public interface TinyTaskConfService {

    /**
     * 配置TinyTasker标记Bean的指定方法
     */
    @NotNull
    Conf config(@NotNull Object bean, @NotNull Method method, @Nullable Object para);

    /**
     * 配置TinyTasker标记Bean的
     */
    @NotNull
    List<Conf> config(@NotNull Object bean);

    /**
     * 从数据库重新载入配置
     */
    @NotNull
    Conf refresh(long id);

    /**
     * 把配置文件数据，写入数据库
     */
    @NotNull
    Conf replace(long id);

    @Data
    class Conf {
        private long id;
        private TaskerProp taskerProp;
    }
}

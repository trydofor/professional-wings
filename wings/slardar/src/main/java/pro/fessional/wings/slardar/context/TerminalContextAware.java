package pro.fessional.wings.slardar.context;

import org.jetbrains.annotations.NotNull;

/**
 * 用于Service层的标记型接口，用于提醒方法副作用，依赖于上下文
 *
 * @author trydofor
 * @since 2022-11-21
 */

public interface TerminalContextAware {

    @NotNull
    default TerminalContext.Context getTerminalContext() {
        return TerminalContext.get();
    }

    default boolean isTerminalActive() {
        return TerminalContext.isActive();
    }
}

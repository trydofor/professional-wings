package pro.fessional.wings.slardar.context;

import org.jetbrains.annotations.NotNull;

/**
 * At the Service layer, indicate the class has side effects, is context-dependent.
 *
 * @author trydofor
 * @since 2022-11-21
 */

public interface TerminalContextAware {

    @NotNull
    default TerminalContext.Context getTerminalContext() {
        return TerminalContext.get();
    }
}

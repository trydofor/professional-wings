package pro.fessional.wings.slardar.servlet.stream;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author trydofor
 * @since 2022-06-07
 */
public interface RequestResponseLogging {

    @Getter @Setter
    class Conf {
        private boolean requestEnable = false;
        private boolean requestPayload = false;
        private boolean responseEnable = false;
        private boolean responsePayload = false;
        private boolean requestLogAfter = false;
    }

    /**
     * Get the config of request logging, `null` means no log
     */
    @Nullable
    default Conf loggingConfig(@NotNull ReuseStreamRequestWrapper req) {
        return null;
    }

    /**
     * handle log before doFilter, that do Not run `dispatch`
     *
     * @param cnf the config
     * @param req the request wrapper
     */
    default void beforeRequest(@NotNull Conf cnf, @NotNull ReuseStreamRequestWrapper req) {
    }

    /**
     * handle log after doFilter, that completed response, not output to client
     *
     * @param cnf the config
     * @param req the request wrapper
     * @param res the response wrapper
     */
    default void afterResponse(@NotNull Conf cnf, @NotNull ReuseStreamRequestWrapper req, @NotNull ReuseStreamResponseWrapper res) {
    }
}

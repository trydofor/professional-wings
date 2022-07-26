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
     * 日志配置，null为不记录
     *
     * @param req 请求
     * @return 日志配置
     */
    @Nullable
    default Conf loggingConfig(@NotNull ReuseStreamRequestWrapper req) {
        return null;
    }

    /**
     * 在doFilter之前，未执行dispatch
     *
     * @param cnf 配置
     * @param req 请求
     */
    default void beforeRequest(@NotNull Conf cnf, @NotNull ReuseStreamRequestWrapper req) {
    }

    /**
     * 在doFilter之后，完成response，未输出到客户端
     *
     * @param cnf 配置
     * @param req 请求
     * @param res 回复
     */
    default void afterResponse(@NotNull Conf cnf, @NotNull ReuseStreamRequestWrapper req, @NotNull ReuseStreamResponseWrapper res) {
    }
}

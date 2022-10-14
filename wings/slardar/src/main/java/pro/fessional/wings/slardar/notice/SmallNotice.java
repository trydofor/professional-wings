package pro.fessional.wings.slardar.notice;

import org.jetbrains.annotations.NotNull;

/**
 * 短小的通知，线程安全
 *
 * @param <T> 配置文件
 * @author trydofor
 * @since 2022-09-29
 */
public interface SmallNotice<T> {

    /**
     * 同步发送，发送成功或失败，或异常
     *
     * @param config  配置文件
     * @param content 正文
     * @return 是否送出
     */
    boolean send(@NotNull T config, @NotNull String content);

    /**
     * 同步发送，fire and forget，不会抛出异常
     *
     * @param config  配置文件
     * @param content 正文
     */
    default void post(@NotNull T config, @NotNull String content) {
        try {
            send(config, content);
        }
        catch (Exception e) {
            // ignore
        }
    }
}

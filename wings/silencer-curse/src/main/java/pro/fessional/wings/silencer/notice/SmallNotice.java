package pro.fessional.wings.silencer.notice;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Small and short messages, thread-safe.
 * encryption is required if the Conf contains any personal information, such as @AesString.
 *
 * @param <C> Configuration, generally message's from and to, etc., but not the contents.
 * @author trydofor
 * @since 2022-09-29
 */
public interface SmallNotice<C> {

    /**
     * get the default config
     */
    @NotNull
    C defaultConfig();

    /**
     * Build a new configuration with the `that` value as priority,
     * and use the default configuration if the `that` item is invalid.
     */
    @Contract("_->new")
    C combineConfig(@Nullable C that);

    /**
     * Provides different configurations depending on the name. combine with default when `combine`.
     * <code>conf == null ? defaultConfig() : combineConfig(conf)</code>
     */
    @Contract("_,true->new")
    C provideConfig(@Nullable String name, boolean combine);

    /**
     * Send synchronously with the default config, result in success or failure, or exception.
     * return false if both subject and content are null
     *
     * @param subject subject
     * @param content content
     * @return whether success
     */
    default boolean send(String subject, String content) {
        return send(defaultConfig(), subject, content);
    }

    /**
     * Send synchronously with the specified config by name, result in success or failure, or exception.
     * return false if both subject and content are null
     *
     * @param name    config's name, with combine=true
     * @param subject subject
     * @param content content
     * @return whether success
     */
    default boolean send(String name, String subject, String content) {
        return send(provideConfig(name, true), subject, content);
    }

    /**
     * Send synchronously with the specified config, result in success or failure, or exception.
     * return false if both subject and content are null
     *
     * @param config  config
     * @param subject subject
     * @param content content
     * @return whether success
     */
    boolean send(C config, String subject, String content);

    /**
     * Send synchronously with default configuration, fire and forget, no exceptions thrown.
     * return false if both subject and content are null
     *
     * @param subject subject
     * @param content content
     * @return whether success
     */
    default boolean post(String subject, String content) {
        return post(defaultConfig(), subject, content);
    }

    /**
     * Send synchronously with the specified config by name, fire and forget, no exceptions thrown.
     * return false if both subject and content are null
     *
     * @param name    config's name, with combine=true
     * @param subject subject
     * @param content content
     * @return whether success
     */
    default boolean post(String name, String subject, String content) {
        return post(provideConfig(name, true), subject, content);
    }

    /**
     * Send synchronously with the specified config, fire and forget, no exceptions thrown.
     * return false if both subject and content are null
     *
     * @param config  config, invalid item can be overridden by the default.
     * @param subject subject
     * @param content content
     * @return whether success
     */
    boolean post(C config, String subject, String content);

    /**
     * Send asynchronously with default configuration, fire and forget, no exceptions thrown.
     *
     * @param subject subject
     * @param content content
     */

    default void emit(String subject, String content) {
        emit(defaultConfig(), subject, content);
    }

    /**
     * Send asynchronously with the specified config by name, fire and forget, no exceptions thrown.
     *
     * @param name    config's name, with combine=true
     * @param subject subject
     * @param content content
     */
    default void emit(String name, String subject, String content) {
        emit(provideConfig(name, true), subject, content);
    }

    /**
     * Send asynchronously with the specified config by name, fire and forget, no exceptions thrown.
     *
     * @param config  config, invalid item can be overridden by the default.
     * @param subject subject
     * @param content content
     */
    void emit(C config, String subject, String content);
}

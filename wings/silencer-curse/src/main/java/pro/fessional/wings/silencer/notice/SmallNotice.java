package pro.fessional.wings.silencer.notice;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.best.DummyBlock;

/**
 * 短小的通知，线程安全。Conf若有个人信息，需要加密，如@AesString
 *
 * @param <C> 配置文件，一般为内容之外的属性，如方式，收发件人等
 * @author trydofor
 * @since 2022-09-29
 */
public interface SmallNotice<C> {

    /**
     * 取得当前默认配置
     */
    @NotNull
    C defaultConfig();

    /**
     * 以that值优先，当that项无效时，使用默认配置，构造一个新配置。
     */
    @NotNull
    C combineConfig(@NotNull C that);

    /**
     * 根据名字，提供不同的配置。combine时与default合并。conf == null ? defaultConfig() : combineConfig(conf);
     */
    @Contract("_,true->!null")
    C provideConfig(@Nullable String name, boolean combine);

    /**
     * 以默认配置同步发送，发送成功或失败，或异常，subject和content同时为null时返回false
     *
     * @param subject 主题
     * @param content 正文
     * @return 是否送出
     */
    default boolean send(String subject, String content) {
        return send(defaultConfig(), subject, content);
    }

    /**
     * 以指定配置同步发送，发送成功或失败，或异常，subject和content同时为null时返回false
     *
     * @param config  配置文件，无效配置项，可由默认配置覆盖。
     * @param subject 主题
     * @param content 正文
     * @return 是否送出
     */
    boolean send(C config, String subject, String content);

    /**
     * 以默认配置同步发送，fire and forget，不会抛出异常
     *
     * @param subject 主题
     * @param content 正文
     */
    default void post(String subject, String content) {
        post(defaultConfig(), subject, content);
    }

    /**
     * 以指定配置同步发送，fire and forget，不会抛出异常
     *
     * @param config  配置文件，无效配置项，可由默认配置覆盖。
     * @param subject 主题
     * @param content 正文
     */
    default void post(C config, String subject, String content) {
        try {
            send(config, subject, content);
        }
        catch (Exception e) {
            DummyBlock.ignore(e);
        }
    }

    /**
     * 以默认配置异步发送，fire and forget，不会抛出异常
     *
     * @param subject 主题
     * @param content 正文
     */

    default void emit(String subject, String content) {
        emit(defaultConfig(), subject, content);
    }

    /**
     * 以指定配置异步发送，fire and forget，不会抛出异常
     *
     * @param config  配置文件，无效配置项，可由默认配置覆盖。
     * @param subject 主题
     * @param content 正文
     */
    void emit(C config, String subject, String content);
}

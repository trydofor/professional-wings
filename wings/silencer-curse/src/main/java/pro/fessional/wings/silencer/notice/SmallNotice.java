package pro.fessional.wings.silencer.notice;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.best.DummyBlock;

/**
 * 短小的通知，线程安全
 *
 * @param <C> 配置文件，一般为内容之外的属性，如方式，收发件人等
 * @author trydofor
 * @since 2022-09-29
 */
public interface SmallNotice<C> {

    /**
     * 同步发送，发送成功或失败，或异常，subject和content同时为null时返回false
     *
     * @param config  配置文件
     * @param subject 主题
     * @param content 正文
     * @return 是否送出
     */
    boolean send(@NotNull C config, String subject, String content);

    /**
     * 同步发送，fire and forget，不会抛出异常
     *
     * @param config  配置文件
     * @param subject 主题
     * @param content 正文
     */
    default void post(@NotNull C config, String subject, String content) {
        try {
            send(config, subject, content);
        }
        catch (Exception e) {
            DummyBlock.ignore(e);
        }
    }
}

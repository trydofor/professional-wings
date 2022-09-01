package pro.fessional.wings.slardar.security;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author trydofor
 * @since 2022-01-18
 */
public interface WingsAuthDetails {
    /**
     * 获取验证的元信息，辅助验证，如request内参数
     *
     * @return 可变map，非线程安全
     */
    @NotNull
    Map<String, String> getMetaData();

    /**
     * 获取build后的实体信息
     *
     * @return 实体信息，如Oauth
     */
    @Nullable
    Object getRealData();
}

package pro.fessional.wings.silencer.tweak;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.evil.ThreadLocalProvider;

/**
 * @author trydofor
 * @since 2024-02-17
 */
public class TtlThreadLocalProvider implements ThreadLocalProvider {
    @Override
    public @NotNull ThreadLocal<?> get() {
        return new TransmittableThreadLocal<>();
    }
}

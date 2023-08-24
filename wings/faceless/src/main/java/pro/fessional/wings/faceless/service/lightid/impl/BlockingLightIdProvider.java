package pro.fessional.wings.faceless.service.lightid.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.mirana.pain.TimeoutRuntimeException;

/**
 * @author trydofor
 * @since 2023-07-17
 */

@Slf4j
@RequiredArgsConstructor
public class BlockingLightIdProvider implements LightIdProvider {

    private final LightIdProvider.Loader loader;

    @Setter @Getter
    private long timeout = 1000;

    @Override
    public long next(@NotNull String name, int block) {
        return next(name, block, timeout);
    }

    @Override
    public long next(@NotNull String name, int block, long timeout) {
        final long throwMs = System.currentTimeMillis() + timeout;

        final Segment seg = loader.require(name, block, 1, true);

        final long now = System.currentTimeMillis();
        if (now > throwMs) {
            throw new TimeoutRuntimeException("loading timeout=" + (now - throwMs + timeout));
        }

        return seg.getHead();
    }
}

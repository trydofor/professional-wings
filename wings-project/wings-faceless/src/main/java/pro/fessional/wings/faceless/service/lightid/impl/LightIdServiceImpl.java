package pro.fessional.wings.faceless.service.lightid.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdAware;



/**
 * @author trydofor
 * @since 2019-05-31
 */
@RequiredArgsConstructor
public class LightIdServiceImpl extends AbstractLightIdService {

    private final LightIdProvider lightIdProvider;
    private final BlockIdProvider blockIdProvider;

    @Override
    public int geBlockId() {
        return blockIdProvider.getBlockId();
    }

    @Override
    public long getId(@NotNull String name, int block) {
        return lightIdProvider.next(name, block);
    }

    @Override
    public long getId(@NotNull Class<? extends LightIdAware> table) {
        return getId(table, blockIdProvider.getBlockId());
    }

    @Override
    public long getId(@NotNull String name) {
        return getId(name, blockIdProvider.getBlockId());
    }
}

package pro.fessional.wings.faceless.service.lightid.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdAware;
import pro.fessional.wings.faceless.service.lightid.LightIdService;

import javax.validation.constraints.NotNull;

/**
 * @author trydofor
 * @since 2019-05-31
 */
@Service
@RequiredArgsConstructor
public class LightIdServiceImpl implements LightIdService {

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
    public long getId(@NotNull Class<? extends LightIdAware> po) {
        return getId(po, blockIdProvider.getBlockId());
    }

    @Override
    public long getId(@NotNull String name) {
        return getId(name, blockIdProvider.getBlockId());
    }
}

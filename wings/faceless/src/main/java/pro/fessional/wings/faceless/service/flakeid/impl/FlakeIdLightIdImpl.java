package pro.fessional.wings.faceless.service.flakeid.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.id.LightId;
import pro.fessional.mirana.id.LightIdUtil;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.faceless.service.flakeid.FlakeIdService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.silencer.datetime.Epochs;

/**
 * <pre>
 * - 41-bit(ms second)
 * - (1) 21-bit(sequence), max=2M/ms
 * - (2) 10-bit(block) + 12-bit(sequence), max=4K/ms
 * </pre>
 *
 * @author trydofor
 * @since 2022-07-20
 */

@RequiredArgsConstructor
public class FlakeIdLightIdImpl implements FlakeIdService {

    public static final int BIT_TMS = 41;
    public static final int BIT_BLK = 10;
    public static final int BIT_SEQ_BLK = 12;
    public static final int BIT_SEQ_WHL = 22;

    public static final long TMS_EPO = Epochs.FlakeId;
    public static final long MAX_TMS = (1L << BIT_TMS) - 1;
    public static final long MAX_BLK = (1L << BIT_BLK) - 1;
    public static final long MAX_SEQ_BLK = (1L << BIT_SEQ_BLK) - 1;
    public static final long MAX_SEQ_WHL = (1L << (BIT_SEQ_WHL - 1)) - 1;

    private final LightIdService lightIdService;

    @Override
    public long getId(@NotNull String name) {
        final long id = lightIdService.getId(name);
        final LightId lid = LightIdUtil.toLightId(id);

        long fid = (ThreadNow.millis() - TMS_EPO) & MAX_TMS;
        if (lid.getBlock() == 0) {
            fid = (fid << BIT_SEQ_WHL) | (lid.getSequence() & MAX_SEQ_WHL);
        }
        else {
            fid = (fid << BIT_BLK) | (lid.getBlock() & MAX_BLK);
            fid = (fid << BIT_SEQ_BLK) | (lid.getSequence() & MAX_SEQ_BLK);
        }

        return fid;
    }
}

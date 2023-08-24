package pro.fessional.wings.slardar.service.lightid;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.mirana.pain.TimeoutRuntimeException;
import pro.fessional.wings.slardar.service.lightid.ser.LidKey;
import pro.fessional.wings.slardar.service.lightid.ser.LidSeg;

import java.util.concurrent.TimeUnit;

import static pro.fessional.wings.slardar.constants.HazelcastConst.MapLightId;

/**
 * @author trydofor
 * @since 2023-07-17
 */

@Slf4j
public class HazelcastLightIdProvider implements LightIdProvider {

    private final Loader loader;
    private final IMap<LidKey, LidSeg> mapper;

    @Setter @Getter
    private long timeout = 1000;

    public HazelcastLightIdProvider(Loader loader, HazelcastInstance hazelcastInstance) {
        this.loader = loader;
        this.mapper = hazelcastInstance.getMap(MapLightId);
    }

    @Override
    public long next(@NotNull String name, int block) {
        return next(name, block, timeout);
    }

    @Override
    public long next(@NotNull String name, int block, long timeout) {
        final long throwMs = System.currentTimeMillis() + timeout;

        final LidKey key = new LidKey(name, block);
        final long next, foot;
        mapper.lock(key, timeout * 2, TimeUnit.MILLISECONDS);
        try {
            final LidSeg seg = mapper.get(key);
            if (seg == null) {
                final Segment sg = loader.require(name, block, 1, false);
                next = sg.getHead();
                foot = sg.getFoot();
                mapper.put(key, new LidSeg(next + 1, foot));
            }
            else {
                next = seg.getNext();
                foot = seg.getFoot();
                if (next == foot) {
                    final Segment sg = loader.require(name, block, 1, false);
                    mapper.put(key, new LidSeg(sg.getHead(), sg.getFoot()));
                }
                else {
                    mapper.put(key, new LidSeg(next + 1, foot));
                }
            }
        }
        finally {
            mapper.unlock(key);
        }

        final long now = System.currentTimeMillis();
        if (now > throwMs) {
            throw new TimeoutRuntimeException("loading timeout=" + (now - throwMs + timeout));
        }

        return next;
    }
}

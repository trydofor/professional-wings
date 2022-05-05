package pro.fessional.wings.slardar.service.flakeid;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.service.flakeid.FlakeIdService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2022-02-24
 */
@RequiredArgsConstructor
public class FlakeIdHazelcastImpl implements FlakeIdService {

    private final HazelcastInstance hazelcastInstance;
    private final Map<String, FlakeIdGenerator> generatorMap = new ConcurrentHashMap<>();

    @Override
    public long getId(@NotNull String name) {
        final FlakeIdGenerator generator = generatorMap.computeIfAbsent(name, k -> hazelcastInstance.getFlakeIdGenerator(name));
        return generator.newId();
    }
}


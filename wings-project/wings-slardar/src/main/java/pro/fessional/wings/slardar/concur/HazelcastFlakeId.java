package pro.fessional.wings.slardar.concur;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

/**
 * @author trydofor
 * @since 2022-02-24
 */
public class HazelcastFlakeId {

    private final FlakeIdGenerator generator;

    public HazelcastFlakeId(HazelcastInstance hazelcastInstance) {
        this(hazelcastInstance, "default");
    }

    public HazelcastFlakeId(HazelcastInstance hazelcastInstance, String name) {
        this.generator = hazelcastInstance.getFlakeIdGenerator(name);
    }

    public long nextId() {
        return generator.newId();
    }
}


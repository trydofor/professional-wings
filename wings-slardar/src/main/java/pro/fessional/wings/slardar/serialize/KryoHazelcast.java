package pro.fessional.wings.slardar.serialize;

import com.esotericsoftware.kryo.io.Input;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

/**
 * @author trydofor
 * @since 2021-06-09
 */
public class KryoHazelcast implements StreamSerializer<Object>/*, HazelcastInstanceAware */ {

    public static final int TYPE_ID = 1979_01_30;

    /*
    private HazelcastInstance hazelcastInstance;

    @Override
    public void setHazelcastInstance(HazelcastInstance ins) {
        this.hazelcastInstance = ins;
    }
    */

    @Override
    public void write(ObjectDataOutput out, Object object) throws IOException {
        final byte[] bytes = KryoSimple.writeClassAndObject(object);
        out.writeByteArray(bytes);
    }

    @Override
    public Object read(ObjectDataInput in) throws IOException {
        final byte[] bytes = in.readByteArray();
        return KryoSimple.readClassAndObject(new Input(bytes));
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }
}

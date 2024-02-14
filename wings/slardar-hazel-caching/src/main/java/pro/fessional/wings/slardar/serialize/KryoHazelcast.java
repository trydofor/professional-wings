package pro.fessional.wings.slardar.serialize;

import com.esotericsoftware.kryo.io.Input;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.data.Null;

import java.io.IOException;

/**
 * @author trydofor
 * @since 2021-06-09
 */
public class KryoHazelcast implements StreamSerializer<Object> {

    public static final int TYPE_ID = 1979_01_30;

    @Override
    public void write(ObjectDataOutput out, @NotNull Object object) throws IOException {
        final byte[] bytes = KryoSimple.writeClassAndObject(object);
        out.writeByteArray(bytes);
    }

    @Override
    public @NotNull Object read(@NotNull ObjectDataInput in) throws IOException {
        final byte[] bytes = Null.notNull(in.readByteArray());
        return KryoSimple.readClassAndObject(new Input(bytes));
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }
}

package pro.fessional.wings.slardar.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * @author trydofor
 * @since 2021-06-09
 */
public class KryoSimple {

    private static final ThreadLocal<Output> output = ThreadLocal.withInitial(() -> {
        return new Output(4096, 1024 * 1024); // 4k-1M
    });

    private static final ThreadLocal<Kryo> kryo = ThreadLocal.withInitial(() -> {
        Kryo ko = new Kryo();
        ko.setReferences(false);
        ko.setRegistrationRequired(false);
        ko.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        return ko;
    });

    public static Kryo getKryo() {
        return kryo.get();
    }

    public static Output getOutput() {
        final Output out = output.get();
        out.reset();
        return out;
    }

    public static <T> T readClassAndObject(byte[] bytes) {
        Input input = new Input(bytes);
        return readClassAndObject(input);
    }

    @SuppressWarnings("unchecked")
    public static <T> T readClassAndObject(Input input) {
        return (T) kryo.get().readClassAndObject(input);
    }

    public static <T> T readObject(Input input, Class<T> clz) {
        return kryo.get().readObject(input, clz);
    }

    public static <T> T readObjectOrNull(Input input, Class<T> clz) {
        return kryo.get().readObjectOrNull(input, clz);
    }

    //
    public static byte[] writeClassAndObject(Object obj) {
        final Output out = getOutput();
        kryo.get().writeClassAndObject(out, obj);
        out.flush();
        return out.toBytes();
    }

    public static void writeClassAndObject(Output out, Object obj) {
        kryo.get().writeClassAndObject(out, obj);
    }

    public static void writeObject(Output out, Object obj) {
        kryo.get().writeObject(out, obj);
    }

    public static void writeObjectOrNull(Output out, Object obj, Class<?> clz) {
        kryo.get().writeObjectOrNull(out, obj, clz);
    }
}

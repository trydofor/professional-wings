package pro.fessional.wings.slardar.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import org.jetbrains.annotations.NotNull;
import org.objenesis.strategy.StdInstantiatorStrategy;
import pro.fessional.mirana.evil.ThreadLocalAttention;
import pro.fessional.mirana.evil.ThreadLocalSoft;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * should register Customizer before use.
 *
 * @author trydofor
 * @since 2021-06-09
 */
public class KryoSimple {

    private static final ThreadLocalSoft<Output> output;

    static {
        try {
            output = new ThreadLocalSoft<>(new ThreadLocal<>()) {
                @Override
                @NotNull
                public Output initValue() {
                    return new Output(4096, 1024 * 1024);
                }
            };
        }
        catch (ThreadLocalAttention e) {
            throw new IllegalStateException(e);
        }
    }

    private static final AtomicReference<Consumer<Kryo>> customizer = new AtomicReference<>();

    /**
     * Register custom serializer. Add User Serializer, mostly implemented by Kryo itself
     *
     * @param cust to customize kryo
     * @see DefaultArraySerializers
     */
    public static void register(Consumer<Kryo> cust) {
        customizer.set(cust);
    }

    /** no leak, for static */
    private static final ThreadLocalSoft<Kryo> kryo;

    static {
        try {
            kryo = new ThreadLocalSoft<>(new ThreadLocal<>()) {
                @Override
                @NotNull public Kryo initValue() {
                    Kryo ko = new Kryo();
                    ko.setReferences(false);
                    ko.setRegistrationRequired(false);
                    ko.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
                    ko.setClassLoader(Thread.currentThread().getContextClassLoader());
                    final var cust = customizer.get();
                    if (cust != null) cust.accept(ko);
                    return ko;
                }
            };
        }
        catch (ThreadLocalAttention e) {
            throw new IllegalStateException(e);
        }
    }

    public static Kryo getKryo() {
        return kryo.use();
    }

    public static Output getOutput() {
        final Output out = output.use();
        out.reset();
        return out;
    }

    public static <T> T readClassAndObject(byte[] bytes) {
        Input input = new Input(bytes);
        return readClassAndObject(input);
    }

    @SuppressWarnings("unchecked")
    public static <T> T readClassAndObject(Input input) {
        return (T) kryo.use().readClassAndObject(input);
    }

    public static <T> T readObject(Input input, Class<T> clz) {
        return kryo.use().readObject(input, clz);
    }

    public static <T> T readObjectOrNull(Input input, Class<T> clz) {
        return kryo.use().readObjectOrNull(input, clz);
    }

    //
    public static byte[] writeClassAndObject(Object obj) {
        final Output out = getOutput();
        kryo.use().writeClassAndObject(out, obj);
        out.flush();
        return out.toBytes();
    }

    public static void writeClassAndObject(Output out, Object obj) {
        kryo.use().writeClassAndObject(out, obj);
    }

    public static void writeObject(Output out, Object obj) {
        kryo.use().writeObject(out, obj);
    }

    public static void writeObjectOrNull(Output out, Object obj, Class<?> clz) {
        kryo.use().writeObjectOrNull(out, obj, clz);
    }
}

package pro.fessional.wings.silencer.support;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.wings.silencer.support.TypeSugar;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Benchmark                     Mode  Cnt      Score      Error   Units
 * TypeSugarMain.describeCache  thrpt    4  20406.578 ± 4018.606  ops/ms
 * TypeSugarMain.describeNew    thrpt    4   1240.069 ±  346.185  ops/ms
 * TypeSugarMain.describeRaw    thrpt    4   1365.260 ±  312.185  ops/ms
 * TypeSugarMain.resolveCache   thrpt    4  21342.189 ± 5840.158  ops/ms
 * TypeSugarMain.resolveNew     thrpt    4   1280.143 ±  214.265  ops/ms
 * TypeSugarMain.resolveRaw     thrpt    4   2061.249 ±  670.345  ops/ms
 * </pre>
 *
 * @author trydofor
 * @since 2024-06-09
 */
@Fork(2)
@Warmup(iterations = 1)
@Measurement(iterations = 2)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class TypeSugarMain {

    @Benchmark
    public void resolveRaw() {
        ResolvableType.forClassWithGenerics(Map.class,
            ResolvableType.forClassWithGenerics(List.class, Long[].class),
            ResolvableType.forClass(String.class)
        );
    }

    @Benchmark
    public void resolveNew() {
        TypeSugar.resolveNew(Map.class, List.class, List.class, Long[].class, String.class);
    }

    @Benchmark
    public void resolveCache() {
        TypeSugar.resolve(Map.class, List.class, List.class, Long[].class, String.class);
    }

    @Benchmark
    public void describeRaw() {
        TypeDescriptor.map(Map.class,
            TypeDescriptor.collection(List.class, TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Long[].class))),
            TypeDescriptor.valueOf(String.class)
        );
    }

    @Benchmark
    public void describeNew() {
        TypeSugar.describeNew(Map.class, List.class, List.class, Long[].class, String.class);
    }

    @Benchmark
    public void describeCache() {
        TypeSugar.describe(Map.class, List.class, List.class, Long[].class, String.class);
    }


    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }
}
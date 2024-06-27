package pro.fessional.wings.silencer.enhance;

import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.silencer.app.conf.TestMergingProp;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2024-06-09
 */
@Slf4j
public class TypeSugarTest {

    @Test
    @TmsLink("C11035")
    void test() throws ClassNotFoundException {

        // Map<String, List<Long[]>
        var a0 = ResolvableType.forClassWithGenerics(Map.class,
            ResolvableType.forClass(String.class),
            ResolvableType.forClassWithGenerics(List.class, Long[].class)
        );
        var a1 = TypeSugar.resolve(Map.class, String.class, List.class, Long[].class);
        log.info("type a1={}", a1);

        var a2 = TypeDescriptor.map(Map.class,
            TypeDescriptor.valueOf(String.class),
            TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Long[].class))
        );
        var a3 = TypeSugar.describe(Map.class, String.class, List.class, Long[].class);

        log.info("type a3={}", a3);
        Assertions.assertEquals(a0, a1);
        Assertions.assertEquals(a2, a3);

        // Map<List<Long[]>,String>
        var b0 = ResolvableType.forClassWithGenerics(Map.class,
            ResolvableType.forClassWithGenerics(List.class, Long[].class),
            ResolvableType.forClass(String.class)
        );
        var b1 = TypeSugar.resolve(Map.class, List.class, Long[].class, String.class);
        log.info("type b1={}", b1);

        var b2 = TypeDescriptor.map(Map.class,
            TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Long[].class)),
            TypeDescriptor.valueOf(String.class)
        );
        var b3 = TypeSugar.describe(Map.class, List.class, Long[].class, String.class);

        log.info("type b3={}", b3);
        Assertions.assertEquals(b0, b1);
        Assertions.assertEquals(b2, b3);

        // Map<List<List<Long[]>>,String>
        var c0 = ResolvableType.forClassWithGenerics(Map.class,
            ResolvableType.forClassWithGenerics(List.class,
                ResolvableType.forClassWithGenerics(List.class, Long[].class)
            ),
            ResolvableType.forClass(String.class)
        );
        var c1 = TypeSugar.resolve(Map.class, List.class, List.class, Long[].class, String.class);
        log.info("type c1={}", c1);

        var c2 = TypeDescriptor.map(Map.class,
            TypeDescriptor.collection(List.class,
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Long[].class))
            ),
            TypeDescriptor.valueOf(String.class)
        );
        var c3 = TypeSugar.describe(Map.class, List.class, List.class, Long[].class, String.class);

        log.info("type c3={}", c3);
        Assertions.assertEquals(c0, c1);
        Assertions.assertEquals(c2, c3);

        var d0 = TypeSugar.resolve(Map.class, List.class, List.class, Long[].class, String.class);
        var d1 = TypeSugar.resolve(Map.class, List.class, List.class, Long[].class, String.class);
        var d2 = TypeSugar.describe(Map.class, List.class, List.class, Long[].class, String.class);
        var d3 = TypeSugar.describe(Map.class, List.class, List.class, Long[].class, String.class);

        log.info("type d1={}", d1);
        log.info("type d3={}", d3);
        Assertions.assertSame(d0, d1);
        Assertions.assertSame(d2, d3);

        //
        testStructs(TypeSugar.resolve(boolean.class));
        testStructs(TypeSugar.resolve(boolean[].class));
        testStructs(TypeSugar.resolve(byte.class));
        testStructs(TypeSugar.resolve(byte[].class));
        testStructs(TypeSugar.resolve(char.class));
        testStructs(TypeSugar.resolve(char[].class));
        testStructs(TypeSugar.resolve(short.class));
        testStructs(TypeSugar.resolve(short[].class));
        testStructs(TypeSugar.resolve(int.class));
        testStructs(TypeSugar.resolve(int[].class));
        testStructs(TypeSugar.resolve(long.class));
        testStructs(TypeSugar.resolve(long[].class));
        testStructs(TypeSugar.resolve(float.class));
        testStructs(TypeSugar.resolve(float[].class));
        testStructs(TypeSugar.resolve(double.class));
        testStructs(TypeSugar.resolve(double[].class));
        testStructs(TypeSugar.resolve(String.class));
        testStructs(TypeSugar.resolve(String[].class));

        testStructs(a0);
        testStructs(b0);
        testStructs(c0);
        testStructs(d0);
        testStructs(TypeSugar.resolve(R.class, TestMergingProp.Pojo.class));
        testStructs(TypeSugar.resolve(R.class));
        testStructs(TypeSugar.resolve(List.class));
        testStructs(TypeSugar.resolve(Map.class));
        testStructs(TypeSugar.resolve(Set.class, LocalDate.class));
    }

    private void testStructs(ResolvableType rt) {
        String str = TypeSugar.outline(rt);
        log.info("structs={}", str);
        ResolvableType rt1 = TypeSugar.resolve(str);
        Assertions.assertEquals(rt, rt1);
    }
}
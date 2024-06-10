package pro.fessional.wings.silencer.enhance;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2024-06-09
 */
public class TypeSugarTest {

    @Test
    @TmsLink("C11035")
    void test() {

        // Map<String, List<Long[]>
        var a0 = ResolvableType.forClassWithGenerics(Map.class,
            ResolvableType.forClass(String.class),
            ResolvableType.forClassWithGenerics(List.class, Long[].class)
        );
        var a1 = TypeSugar.resolve(Map.class, String.class, List.class, Long[].class);

        var a2 = TypeDescriptor.map(Map.class,
            TypeDescriptor.valueOf(String.class),
            TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Long[].class))
        );
        var a3 = TypeSugar.describe(Map.class, String.class, List.class, Long[].class);

        Assertions.assertEquals(a0, a1);
        Assertions.assertEquals(a2, a3);

        // Map<List<Long[]>,String>
        var b0 = ResolvableType.forClassWithGenerics(Map.class,
            ResolvableType.forClassWithGenerics(List.class, Long[].class),
            ResolvableType.forClass(String.class)
        );
        var b1 = TypeSugar.resolve(Map.class, List.class, Long[].class, String.class);

        var b2 = TypeDescriptor.map(Map.class,
            TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Long[].class)),
            TypeDescriptor.valueOf(String.class)
        );
        var b3 = TypeSugar.describe(Map.class, List.class, Long[].class, String.class);

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

        var c2 = TypeDescriptor.map(Map.class,
            TypeDescriptor.collection(List.class,
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Long[].class))
            ),
            TypeDescriptor.valueOf(String.class)
        );
        var c3 = TypeSugar.describe(Map.class, List.class, List.class, Long[].class, String.class);

        Assertions.assertEquals(c0, c1);
        Assertions.assertEquals(c2, c3);

        var d0 = TypeSugar.resolve(Map.class, List.class, List.class, Long[].class, String.class);
        var d1 = TypeSugar.resolve(Map.class, List.class, List.class, Long[].class, String.class);
        var d2 = TypeSugar.describe(Map.class, List.class, List.class, Long[].class, String.class);
        var d3 = TypeSugar.describe(Map.class, List.class, List.class, Long[].class, String.class);
        Assertions.assertSame(d0, d1);
        Assertions.assertSame(d2, d3);
    }
}
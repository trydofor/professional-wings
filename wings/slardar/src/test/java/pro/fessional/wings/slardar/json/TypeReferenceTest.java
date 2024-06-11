package pro.fessional.wings.slardar.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.wings.silencer.enhance.TypeSugar;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2024-06-09
 */
@Slf4j
public class TypeReferenceTest {

    @Test
    @TmsLink("C13127")
    public void testType() {
        {
            Type fastRef = new com.alibaba.fastjson2.TypeReference<Map<String, List<Long[]>>>() {}.getType();
            Type jackRef = new com.fasterxml.jackson.core.type.TypeReference<Map<String, List<Long[]>>>() {}.getType();

            Type descType = TypeDescriptor.map(Map.class,
                TypeDescriptor.valueOf(String.class),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Long[].class))
            ).getResolvableType().getType();

            Type resoType = ResolvableType.forClassWithGenerics(Map.class,
                ResolvableType.forClass(String.class),
                ResolvableType.forClassWithGenerics(List.class, Long[].class)
            ).getType();

            Type helpType = TypeSugar.resolve(Map.class, String.class, List.class, Long[].class).getType();


            log.info("fastRef={}", fastRef);
            log.info("jackRef={}", jackRef);
            log.info("descType={}", descType);
            log.info("resoType={}", resoType);
            log.info("helpType={}", helpType);

            Assertions.assertEquals(fastRef, jackRef);
            Assertions.assertEquals(fastRef, descType);
            Assertions.assertEquals(fastRef, resoType);
            Assertions.assertEquals(fastRef, helpType);

            // check cache
            Type fastRef1 = new com.alibaba.fastjson2.TypeReference<Map<String, List<Long[]>>>() {}.getType();
            Type jackRef1 = new com.fasterxml.jackson.core.type.TypeReference<Map<String, List<Long[]>>>() {}.getType();

            Type descType1 = TypeDescriptor.map(Map.class,
                TypeDescriptor.valueOf(String.class),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Long[].class))
            ).getResolvableType().getType();

            Type resoType1 = ResolvableType.forClassWithGenerics(Map.class,
                ResolvableType.forClass(String.class),
                ResolvableType.forClassWithGenerics(List.class, Long[].class)
            ).getType();

            Type helpType1 = TypeSugar.resolve(Map.class, String.class, List.class, Long[].class).getType();

            Assertions.assertNotSame(fastRef, fastRef1);
            Assertions.assertNotSame(jackRef, jackRef1);
            Assertions.assertNotSame(descType, descType1);
            Assertions.assertNotSame(resoType, resoType1);
            Assertions.assertSame(helpType, helpType1);
        }
        {
            Type fastRef = new com.alibaba.fastjson2.TypeReference<Map<List<Long[]>, String>>() {}.getType();
            Type jackRef = new com.fasterxml.jackson.core.type.TypeReference<Map<List<Long[]>, String>>() {}.getType();

            Type descType = TypeDescriptor.map(Map.class,
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Long[].class)),
                TypeDescriptor.valueOf(String.class)
            ).getResolvableType().getType();

            Type resoType = ResolvableType.forClassWithGenerics(Map.class,
                ResolvableType.forClassWithGenerics(List.class, Long[].class),
                ResolvableType.forClass(String.class)
            ).getType();

            Type helpType = TypeSugar.resolve(Map.class, List.class, Long[].class, String.class).getType();

            log.info("fastRef={}", fastRef);
            log.info("jackRef={}", jackRef);
            log.info("descType={}", descType);
            log.info("resoType={}", resoType);
            log.info("helpType={}", helpType);

            Assertions.assertEquals(fastRef, jackRef);
            Assertions.assertEquals(fastRef, descType);
            Assertions.assertEquals(fastRef, resoType);
            Assertions.assertEquals(fastRef, helpType);
        }
        {
            Type fastRef = new com.alibaba.fastjson2.TypeReference<Map<List<List<Long[]>>, String>>() {}.getType();
            Type jackRef = new com.fasterxml.jackson.core.type.TypeReference<Map<List<List<Long[]>>, String>>() {}.getType();

            Type descType = TypeDescriptor.map(Map.class,
                TypeDescriptor.collection(List.class, TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Long[].class))),
                TypeDescriptor.valueOf(String.class)
            ).getResolvableType().getType();

            Type resoType = ResolvableType.forClassWithGenerics(Map.class,
                ResolvableType.forClassWithGenerics(List.class, ResolvableType.forClassWithGenerics(List.class, Long[].class)),
                ResolvableType.forClass(String.class)
            ).getType();

            Type helpType = TypeSugar.resolve(Map.class, List.class, List.class, Long[].class, String.class).getType();

            log.info("fastRef={}", fastRef);
            log.info("jackRef={}", jackRef);
            log.info("descType={}", descType);
            log.info("resoType={}", resoType);
            log.info("resoType={}", helpType);

            Assertions.assertEquals(fastRef, jackRef);
            Assertions.assertEquals(fastRef, descType);
            Assertions.assertEquals(fastRef, resoType);
            Assertions.assertEquals(fastRef, helpType);
        }
        {
            Type fastRef = new com.alibaba.fastjson2.TypeReference<Map<List<List<Long[]>>, List<String>>>() {}.getType();
            Type jackRef = new com.fasterxml.jackson.core.type.TypeReference<Map<List<List<Long[]>>, List<String>>>() {}.getType();

            Type descType = TypeDescriptor.map(Map.class,
                TypeDescriptor.collection(List.class, TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Long[].class))),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(String.class))
            ).getResolvableType().getType();

            Type resoType = ResolvableType.forClassWithGenerics(Map.class,
                ResolvableType.forClassWithGenerics(List.class, ResolvableType.forClassWithGenerics(List.class, Long[].class)),
                ResolvableType.forClassWithGenerics(List.class, ResolvableType.forClass(String.class))
            ).getType();

            Type helpType = TypeSugar.resolve(Map.class, List.class, List.class, Long[].class, List.class, String.class).getType();

            log.info("fastRef={}", fastRef);
            log.info("jackRef={}", jackRef);
            log.info("descType={}", descType);
            log.info("resoType={}", resoType);
            log.info("resoType={}", helpType);

            Assertions.assertEquals(fastRef, jackRef);
            Assertions.assertEquals(fastRef, descType);
            Assertions.assertEquals(fastRef, resoType);
            Assertions.assertEquals(fastRef, helpType);
        }

        // java type
        {
            TypeFactory tf = new ObjectMapper().getTypeFactory();
            Type jackRef = new com.fasterxml.jackson.core.type.TypeReference<Map<List<List<Long[]>>, String>>() {}.getType();
            JavaType jt0 = tf.constructType(jackRef);
            JavaType jt1 = tf.constructType(TypeSugar.type(Map.class, List.class, List.class, Long[].class, String.class));
            Assertions.assertEquals(jt0, jt1);


            Type tp0 = new com.google.common.reflect.TypeToken<List<String>>(){}.getType();
            Type tp1 = new com.alibaba.fastjson2.TypeReference<List<String>>() {}.getType();
            Type tp2 = new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {}.getType();
            Type tp3 = ResolvableType.forClassWithGenerics(List.class, String.class).getType();
            Type tp4 = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(String.class)).getResolvableType().getType();
            Type tp5 = TypeSugar.type(List.class, String.class);

            Assertions.assertEquals(tp0, tp1);
            Assertions.assertEquals(tp0, tp2);
            Assertions.assertEquals(tp0, tp3);
            Assertions.assertEquals(tp0, tp4);
            Assertions.assertEquals(tp0, tp5);
        }
    }
}

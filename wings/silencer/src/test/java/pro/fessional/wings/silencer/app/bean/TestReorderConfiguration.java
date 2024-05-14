package pro.fessional.wings.silencer.app.bean;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @since 2024-05-11
 */
@ConditionalWingsEnabled
@Configuration(proxyBeanMethods = false)
public class TestReorderConfiguration {

    @Bean
    @ConditionalWingsEnabled
    @Order(2)
    public PlainClass plainClass1() {
        return new PlainClass(1);
    }

    @Bean
    @ConditionalWingsEnabled
    @Order(1)
    public PlainClass plainClass2() {
        return new PlainClass(2);
    }

    @Bean
    @ConditionalWingsEnabled
    @Order(2)
    public GetterClass getterClass1() {
        return new GetterClass(1);
    }

    @Bean
    @ConditionalWingsEnabled
    @Order(1)
    public GetterClass getterClass2() {
        return new GetterClass(2);
    }


    @Bean
    @ConditionalWingsEnabled
    @Order(2)
    public OrderedClass orderedClass1() {
        return new OrderedClass(1);
    }

    @Bean
    @ConditionalWingsEnabled
    @Order(1)
    public OrderedClass orderedClass2() {
        return new OrderedClass(2);
    }


    @RequiredArgsConstructor
    public static class PlainClass {
        private final int order;

        @Override
        public String toString() {
            return String.valueOf(order);
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class GetterClass {
        private final int order;

        @Override
        public String toString() {
            return String.valueOf(order);
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class OrderedClass implements Ordered {
        private final int order;

        @Override
        public String toString() {
            return String.valueOf(order);
        }
    }
}

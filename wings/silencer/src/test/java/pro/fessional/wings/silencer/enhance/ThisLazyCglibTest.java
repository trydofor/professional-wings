package pro.fessional.wings.silencer.enhance;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import pro.fessional.wings.silencer.app.service.TestThisLazyService;
import pro.fessional.wings.silencer.app.service.impl.TestThisLazyServiceEnhanced;
import pro.fessional.wings.silencer.app.service.impl.TestThisLazyServiceItself;
import pro.fessional.wings.silencer.app.service.impl.TestThisLazyServiceProxy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author trydofor
 * @since 2024-05-16
 */
@SpringBootTest
@EnableAsync(proxyTargetClass = true)
@Slf4j
class ThisLazyCglibTest {

    @Setter(onMethod_ = {@Autowired})
    protected TestThisLazyService testThisLazyServiceProxy;
    @Setter(onMethod_ = {@Autowired})
    protected TestThisLazyServiceItself testThisLazyServiceItself;
    @Setter(onMethod_ = {@Autowired})
    protected TestThisLazyServiceEnhanced testThisLazyServiceEnhanced;

    @TmsLink("C11033")
    @Test
    public void testThisLazy() throws ExecutionException, InterruptedException {
        Class<?> clz1 = testThisLazyServiceProxy.getClass();
        Class<?> thz1 = ((ThisLazyAware<?>) testThisLazyServiceProxy).thisLazyType();
        CompletableFuture<Class<?>> rt1 = testThisLazyServiceProxy.thisClass();

        Assertions.assertTrue(TestThisLazyServiceProxy.class.isAssignableFrom(clz1));
        Assertions.assertEquals(TestThisLazyService.class, thz1);
        Assertions.assertEquals(TestThisLazyServiceProxy.class, rt1.get());

        Class<?> clz2 = testThisLazyServiceItself.getClass();
        Class<?> thz2 = testThisLazyServiceItself.thisLazyType();
        CompletableFuture<Class<?>> rt2 = testThisLazyServiceItself.thisClass();

        Assertions.assertEquals(TestThisLazyServiceItself.class, clz2);
        Assertions.assertEquals(TestThisLazyServiceItself.class, thz2);
        Assertions.assertEquals(TestThisLazyServiceItself.class, rt2.get());

        Class<?> clz3 = testThisLazyServiceEnhanced.getClass();
        Class<?> thz3 = testThisLazyServiceEnhanced.thisLazyType();
        CompletableFuture<Class<?>> rt3 = testThisLazyServiceEnhanced.thisClass();

        Assertions.assertTrue(TestThisLazyServiceEnhanced.class.isAssignableFrom(clz3));
        Assertions.assertEquals(Object.class, thz3);
        Assertions.assertEquals(TestThisLazyServiceEnhanced.class, rt3.get());
    }
}
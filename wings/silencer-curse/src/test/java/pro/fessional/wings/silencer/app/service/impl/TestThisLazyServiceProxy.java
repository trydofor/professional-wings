package pro.fessional.wings.silencer.app.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.fessional.wings.silencer.app.service.TestThisLazyService;
import pro.fessional.wings.silencer.enhance.ThisLazy;

import java.util.concurrent.CompletableFuture;

/**
 * @author trydofor
 * @since 2024-05-16
 */
@Primary
@Service
public class TestThisLazyServiceProxy extends ThisLazy<TestThisLazyService> implements TestThisLazyService {

    @Override
    @Async
    public CompletableFuture<Class<?>> thisClass() {
        return CompletableFuture.completedFuture(TestThisLazyServiceProxy.class);
    }
}

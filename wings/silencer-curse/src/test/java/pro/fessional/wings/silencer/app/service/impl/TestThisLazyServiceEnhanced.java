package pro.fessional.wings.silencer.app.service.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.fessional.wings.silencer.enhance.ThisLazy;

import java.util.concurrent.CompletableFuture;

/**
 * @author trydofor
 * @since 2024-05-16
 */
@Service
public class TestThisLazyServiceEnhanced extends ThisLazy<TestThisLazyServiceEnhanced> {

    @Async
    public CompletableFuture<Class<?>> thisClass() {
        return CompletableFuture.completedFuture(TestThisLazyServiceEnhanced.class);
    }

    @Override
    public @NotNull Class<?> thisLazyType() {
        return Object.class;
    }
}

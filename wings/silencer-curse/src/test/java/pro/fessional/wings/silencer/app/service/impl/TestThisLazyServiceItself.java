package pro.fessional.wings.silencer.app.service.impl;

import org.springframework.stereotype.Service;
import pro.fessional.wings.silencer.app.service.TestThisLazyService;
import pro.fessional.wings.silencer.enhance.ThisLazy;

import java.util.concurrent.CompletableFuture;

/**
 * @author trydofor
 * @since 2024-05-16
 */
@Service
public class TestThisLazyServiceItself extends ThisLazy<TestThisLazyServiceItself> implements TestThisLazyService {

    @Override
    public CompletableFuture<Class<?>> thisClass() {
        return CompletableFuture.completedFuture(TestThisLazyServiceItself.class);
    }
}

package pro.fessional.wings.silencer.app.service;

import java.util.concurrent.CompletableFuture;

/**
 * @author trydofor
 * @since 2024-05-16
 */
public interface TestThisLazyService {
    CompletableFuture<Class<?>> thisClass();
}

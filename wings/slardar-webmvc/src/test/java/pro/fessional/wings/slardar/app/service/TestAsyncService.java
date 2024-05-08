package pro.fessional.wings.slardar.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;

/**
 * @author trydofor
 * @since 2022-12-03
 */
@Service
@Slf4j
public class TestAsyncService {

    public enum AsyncType {
        UncaughtException,
        Return,
        FailedFuture
    }

    public static String UserIdUncaughtException = "asyncType UncaughtException";
    public static String UserIdFailedFuture = "asyncType FailedFuture";

    @Async
    public CompletableFuture<String> asyncType(AsyncType type) {
        final String name = Thread.currentThread().getName();
        if (name.contains("exec-")) {
            log.info("asyncType={}", type);
        }
        else {
            log.error("bad thread name prefix, asyncType should contain 'exec-'");
        }

        return switch (type) {
            case UncaughtException -> throw new RuntimeException(UserIdUncaughtException);
            case Return -> CompletableFuture.completedFuture(type.name());
            case FailedFuture -> CompletableFuture.failedFuture(new RuntimeException(UserIdFailedFuture));
        };
    }

    public static String VoidUncaughtException = "asyncVoid UncaughtException";
    public static String VoidFailedFuture = "asyncVoid FailedFuture";

    @Async
    public void asyncVoid(AsyncType type) {
        syncResult(type);
    }

    @Async
    public void badAsync() {
        // exception handled by AsyncConfigurer#getAsyncUncaughtExceptionHandler
    }

    @Async
    public CompletableFuture<Void> goodAsync() {
        // exception handled by caller via AOP/ExceptionHandler
        return CompletableFuture.completedFuture(null);
    }

    public String syncResult(AsyncType type) {
        final String name = Thread.currentThread().getName();
        if (name.contains("exec-")) {
            log.info("asyncVoid");
        }
        else {
            log.error("bad thread name prefix, asyncVoid should contain 'exec-'");
        }

        if (type == AsyncType.UncaughtException) {
            throw new RuntimeException(VoidUncaughtException);
        }
        else if (type == AsyncType.FailedFuture) {
            throw new RuntimeException(VoidFailedFuture);
        }
        return type.name();
    }

    @Async
    public void asyncDefer(DeferredResult<String> result, AsyncType type) {
        final String name = Thread.currentThread().getName();
        if (name.contains("exec-")) {
            log.info("asyncVoid");
        }
        else {
            log.error("bad thread name prefix, asyncVoid should contain 'exec-'");
        }

        if (type == AsyncType.UncaughtException) {
            throw new RuntimeException(VoidUncaughtException);
        }
        else if (type == AsyncType.FailedFuture) {
            result.setErrorResult(new RuntimeException(VoidFailedFuture));
        }
        else {
            result.setResult(type.name());
        }
    }
}

package pro.fessional.wings.slardar.app.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.security.DefaultUserId;

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

    public static String UserIdUncaughtException = "asyncUserId UncaughtException";
    public static String UserIdFailedFuture = "asyncUserId FailedFuture";

    @Async
    public CompletableFuture<Long> userIdAsync(AsyncType type) {
        final String name = Thread.currentThread().getName();
        final long uid;
        if (name.contains("exec-")) {
            final TerminalContext.Context ctx = TerminalContext.get();
            uid = ctx.getUserId();
            log.info("asyncUserId={}", uid);
        }
        else {
            Assertions.fail("bad thread prefix, should start with 'exec-'");
            uid = DefaultUserId.Null;
        }

        return switch (type) {
            case UncaughtException -> throw new RuntimeException(UserIdUncaughtException);
            case Return -> CompletableFuture.completedFuture(uid);
            case FailedFuture -> CompletableFuture.failedFuture(new RuntimeException(UserIdFailedFuture));
        };
    }

    public Long userId(AsyncType type) {
        final String name = Thread.currentThread().getName();
        final long uid;
        if (name.contains("exec-")) {
            final TerminalContext.Context ctx = TerminalContext.get();
            uid = ctx.getUserId();
            log.info("asyncUserId={}", uid);
        }
        else {
            Assertions.fail("bad thread prefix, should start with 'exec-'");
            uid = DefaultUserId.Null;
        }

        return switch (type) {
            case UncaughtException -> throw new RuntimeException(UserIdUncaughtException);
            case Return -> uid;
            case FailedFuture -> throw new RuntimeException(UserIdFailedFuture);
        };
    }

    public static String VoidUncaughtException = "asyncVoid UncaughtException";

    @Async
    public void voidAsync(AsyncType type) {
        final String name = Thread.currentThread().getName();
        if (name.contains("exec-")) {
            log.info("asyncVoid");
        }
        else {
            Assertions.fail("bad thread prefix, should start with 'exec-'");
        }

        if (type == AsyncType.UncaughtException) {
            throw new RuntimeException(VoidUncaughtException);
        }
    }
}

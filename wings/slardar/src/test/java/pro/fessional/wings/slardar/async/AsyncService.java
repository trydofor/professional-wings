package pro.fessional.wings.slardar.async;

import lombok.extern.slf4j.Slf4j;
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
public class AsyncService {

    @Async
    public CompletableFuture<Long> asyncUserId() {
        final String name = Thread.currentThread().getName();
        final long uid;
        if (name.startsWith("win-async-")) {
            final TerminalContext.Context ctx = TerminalContext.get();
            uid = ctx.getUserId();
            log.info("AsyncService userId={}", uid);
        }
        else {
            log.error("bad thread name prefix, should start with 'win-async-'");
            uid = DefaultUserId.Null;
        }
        return CompletableFuture.completedFuture(uid);
    }
}

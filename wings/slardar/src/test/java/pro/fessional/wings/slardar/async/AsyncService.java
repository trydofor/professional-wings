package pro.fessional.wings.slardar.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.context.TerminalContext;

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
        final TerminalContext.Context ctx = TerminalContext.get();
        log.info("AsyncService userId={}", ctx.getUserId());
        return CompletableFuture.completedFuture(ctx.getUserId());
    }
}

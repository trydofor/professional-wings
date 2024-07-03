package pro.fessional.wings.warlock.app.controller;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.pain.MessageException;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.faceless.service.journal.impl.DefaultJournalService;
import pro.fessional.wings.slardar.async.AsyncHelper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author trydofor
 * @since 2024-05-07
 */
@Slf4j
@RestController
public class TestJournalController {

    @Setter(onMethod_ = { @Autowired })
    protected DefaultJournalService defaultJournalService;

    @Setter(onMethod_ = { @Autowired(required = false), @Qualifier("plainPoolTaskExecutor") })
    protected ThreadPoolTaskExecutor plainPoolTaskExecutor;

    private final AtomicLong dummyLightId = new AtomicLong(1);
    private int aliveSecond = -1;

    @RequestMapping(value = "/test/ttl-journal.json")
    public CompletableFuture<Long> testTtlContext(@RequestParam("i") int i, @RequestParam("t") boolean t) {
        return t ? AsyncHelper.Async(() -> recurExecute(i))
            : plainPoolTaskExecutor.submitCompletable(() -> recurExecute(i));
    }

    private Long recurExecute(int t) {
        if (t % 5 != 0) {
            return defaultJournalService.submit(aliveSecond, dummyLightId, "", null, null, null, d -> {
                recurExecute(t - 1);
                return d.getId();
            });
        }
        else {
            return defaultJournalService.submit(aliveSecond, dummyLightId, "", null, null, null, d -> {
                Sleep.ignoreInterrupt(500);
                if (t % 7 == 0) throw new MessageException("error7=" + t);
                return d.getId();
            });
        }
    }

}

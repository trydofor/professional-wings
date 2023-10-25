package pro.fessional.wings.slardar.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.context.TerminalContext;

/**
 * @author trydofor
 * @since 2022-12-03
 */
@Service
@Slf4j
public class ScheduleService {

    @Scheduled(fixedRate = 1000)
    public void scheduleRate() {
        final TerminalContext.Context ctx = TerminalContext.get(false);
        log.info("ScheduleService userId={}", ctx.getUserId());
    }
}

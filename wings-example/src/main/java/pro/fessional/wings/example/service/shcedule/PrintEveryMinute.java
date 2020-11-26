package pro.fessional.wings.example.service.shcedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author trydofor
 * @since 2020-11-25
 */
@Service
@Slf4j
public class PrintEveryMinute {

    @Scheduled(cron = "0 * * * * ?")
    public void print(){
        log.info("hello word, ");
    }
}

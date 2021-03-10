package pro.fessional.wings.slardar.concur;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.flow.DoubleKillException;
import pro.fessional.wings.slardar.service.DoubleKillService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author trydofor
 * @since 2021-03-09
 */

@SpringBootTest(properties = {"debug = true"})
class DoubleKillAroundTest {

    @Autowired
    private DoubleKillService doubleKillService;

    @Test
    void doubleKillArg() throws InterruptedException {
        new Thread(() -> {
            System.out.println("before thread call");
            doubleKillService.sleepSecond("sleep", 10);
            System.out.println("after  thread call");
        }).start();

        Thread.sleep(1000);
        try {
            System.out.println("before main call");
            doubleKillService.sleepSecond("sleep", 10);
            System.out.println("after  main call");
            fail();
        } catch (DoubleKillException e) {
            assertTrue(true);
        }
    }

    @Test
    void doubleKillStr() throws InterruptedException {
        new Thread(() -> {
            System.out.println("before thread call");
            doubleKillService.sleepSecondStr("sleep", 10);
            System.out.println("after  thread call");
        }).start();

        Thread.sleep(1000);
        try {
            System.out.println("before main call");
            doubleKillService.sleepSecondStr("sleep", 10);
            System.out.println("after  main call");
            fail();
        } catch (DoubleKillException e) {
            assertTrue(true);
        }
    }

    @Test
    void doubleKillExp() throws InterruptedException {
        new Thread(() -> {
            System.out.println("before thread call");
            doubleKillService.sleepSecondExp("sleep", 10);
            System.out.println("after  thread call");
        }).start();

        Thread.sleep(1000);
        try {
            System.out.println("before main call");
            doubleKillService.sleepSecondExp("sleep", 10);
            System.out.println("after  main call");
            fail();
        } catch (DoubleKillException e) {
            assertTrue(true);
        }
    }
}

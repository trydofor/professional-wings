package pro.fessional.wings.slardar.service;

import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.concur.DoubleKill;

/**
 * @author trydofor
 * @since 2021-03-09
 */
@Service
public class DoubleKillService {

    @DoubleKill
    public String sleepSecond(String type, int s) {
        try {
            Thread.sleep(s * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return type + "-" + s;
    }

    @DoubleKill("static-key")
    public String sleepSecondStr(String type, int s) {
        try {
            Thread.sleep(s * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return type + "-" + s;
    }

    @DoubleKill(expression = "#root.methodName + #root.targetClass + #p0 + '-' + #p1 * 1000")
    public String sleepSecondExp(String type, int s) {
        try {
            Thread.sleep(s * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return type + "-" + s;
    }
}

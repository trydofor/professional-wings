package pro.fessional.wings.oracle.spring.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author trydofor
 * @since 2019-05-30
 */
@Component
@ConfigurationProperties("wings.light-id.insert")
public class WingsLightIdInsertProperties {

    private boolean auto = true;
    private long next = 1;
    private int step = 100;

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public long getNext() {
        return next;
    }

    public void setNext(long next) {
        this.next = next;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}

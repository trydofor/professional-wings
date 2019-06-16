package pro.fessional.wings.faceless.spring.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author trydofor
 * @since 2019-05-30
 */
@Component
@ConfigurationProperties("wings.light-id.loader")
public class WingsLightIdLoaderProperties {

    private long timeout = 1000;
    private int maxError = 5;
    private int maxCount = 10000;
    private long errAlive = 120000;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getMaxError() {
        return maxError;
    }

    public void setMaxError(int maxError) {
        this.maxError = maxError;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public long getErrAlive() {
        return errAlive;
    }

    public void setErrAlive(long errAlive) {
        this.errAlive = errAlive;
    }
}

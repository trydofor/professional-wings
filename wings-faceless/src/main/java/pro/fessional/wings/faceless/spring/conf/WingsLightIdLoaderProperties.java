package pro.fessional.wings.faceless.spring.conf;

import lombok.Data;

/**
 * @author trydofor
 * @since 2019-05-30
 */
@Data
public class WingsLightIdLoaderProperties {

    private long timeout = 1000;
    private int maxError = 5;
    private int maxCount = 10000;
    private long errAlive = 120000;
}

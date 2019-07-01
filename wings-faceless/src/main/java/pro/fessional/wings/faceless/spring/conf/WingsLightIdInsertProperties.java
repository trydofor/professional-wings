package pro.fessional.wings.faceless.spring.conf;

import lombok.Data;

/**
 * @author trydofor
 * @since 2019-05-30
 */
@Data
public class WingsLightIdInsertProperties {

    private boolean auto = true;
    private long next = 1;
    private int step = 100;
}

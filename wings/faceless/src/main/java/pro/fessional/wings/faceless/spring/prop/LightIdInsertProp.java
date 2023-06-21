package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @see #Key
 * @since 2019-05-30
 */
@Data
@ConfigurationProperties(LightIdInsertProp.Key)
public class LightIdInsertProp {

    public static final String Key = "wings.faceless.lightid.insert";

    /**
     * If the current ID of name and block does not exist, insert new one or throw an exception.
     *
     * @see #Key$auto
     */
    private boolean auto = true;
    public static final String Key$auto = Key + ".auto";

    /**
     * The first value when auto-insert, recommended to start with 1000, as the  value below is used manually.
     *
     * @see #Key$next
     */
    private long next = 1000;
    public static final String Key$next = Key + ".next";

    /**
     * The step value when auto-insert.
     *
     * @see #Key$step
     */
    private int step = 100;
    public static final String Key$step = Key + ".step";

}

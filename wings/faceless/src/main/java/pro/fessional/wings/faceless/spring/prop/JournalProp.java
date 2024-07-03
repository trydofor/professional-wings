package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.transaction.annotation.Propagation;

/**
 * @author trydofor
 * @since 2024-07-05
 */
@Data
@ConfigurationProperties(JournalProp.Key)
public class JournalProp {
    public static final String Key = "wings.faceless.journal";

    /**
     * transaction to create new Journal
     *
     * @see #Key$propagation
     */
    private Propagation propagation = Propagation.REQUIRES_NEW;
    public static final String Key$propagation = Key + ".propagation";

    /**
     * <pre>
     * create new journal if the existing is older than alive,
     * * negative - use the old
     * * zero - new one every time
     * * positive - new one if older
     * </pre>
     *
     * @see #Key$alive
     */
    private int alive = 300;
    public static final String Key$alive = Key + ".alive";
}

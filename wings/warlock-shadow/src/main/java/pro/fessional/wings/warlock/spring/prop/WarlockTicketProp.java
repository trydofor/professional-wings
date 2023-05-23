package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 * wings-warlock-ticket-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-17
 */
@Data
@ConfigurationProperties(WarlockTicketProp.Key)
public class WarlockTicketProp {

    public static final String Key = "wings.warlock.ticket";

    /**
     * PubMod of ticket.
     *
     * @see #Key$pubMod
     */
    private String pubMod = "win";
    public static final String Key$pubMod = Key + ".pub-mod";

    /**
     * authorization code expired time, default 60 seconds.
     *
     * @see #Key$codeTtl
     */
    private Duration codeTtl = Duration.ofSeconds(60);
    public static final String Key$codeTtl = Key + ".code-ttl";

    /**
     * max number of valid authorization codes, default 3.
     *
     * @see #Key$codeMax
     */
    private int codeMax = 3;
    public static final String Key$codeMax = Key + ".code-max";

    /**
     * access token expired time, default 1 hour.
     *
     * @see #Key$tokenTtl
     */
    private Duration tokenTtl = Duration.ofHours(1);
    public static final String Key$tokenTtl = Key + ".token-ttl";

    /**
     * max number of valid access token, default 5.
     *
     * @see #Key$tokenMax
     */
    private int tokenMax = 5;
    public static final String Key$tokenMax = Key + ".token-max";

    /**
     * static config of client login.
     *
     * @see #Key$client
     */
    private Map<String, WarlockTicketService.Pass> client = Collections.emptyMap();
    public static final String Key$client = Key + ".client";

}

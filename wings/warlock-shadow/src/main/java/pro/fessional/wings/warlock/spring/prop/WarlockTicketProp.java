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
     * Aes256的key
     *
     * @see #Key$aesKey
     */
    private String aesKey = "";
    public static final String Key$aesKey = Key + ".aes-key";


    /**
     * ticket的PubMod
     *
     * @see #Key$pubMod
     */
    private String pubMod = "win";
    public static final String Key$pubMod = Key + ".pub-mod";

    /**
     * authorization code 过期时间，默认60秒
     *
     * @see #Key$codeTtl
     */
    private Duration codeTtl = Duration.ofSeconds(60);
    public static final String Key$codeTtl = Key + ".code-ttl";

    /**
     * 有效authorization code的最大数量，默认3
     *
     * @see #Key$codeMax
     */
    private int codeMax = 3;
    public static final String Key$codeMax = Key + ".code-max";

    /**
     * access token的过期时间，默认1小时
     *
     * @see #Key$tokenTtl
     */
    private Duration tokenTtl = Duration.ofHours(1);
    public static final String Key$tokenTtl = Key + ".token-ttl";

    /**
     * 有效access token的最大数量，默认5
     *
     * @see #Key$tokenMax
     */
    private int tokenMax = 5;
    public static final String Key$tokenMax = Key + ".token-max";

    /**
     * 静态配置 client 登录信息
     *
     * @see #Key$client
     */
    private Map<String, WarlockTicketService.Pass> client = Collections.emptyMap();
    public static final String Key$client = Key + ".client";

}

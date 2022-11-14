package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

/**
 * wings-warlock-apiauth-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-17
 */
@Data
@ConfigurationProperties(WarlockApiAuthProp.Key)
public class WarlockApiAuthProp {

    public static final String Key = "wings.warlock.apiauth";

    /**
     * Header name of Client Id
     *
     * @see #Key$clientHeader
     */
    private String clientHeader = "Auth-Client";
    public static final String Key$clientHeader = Key + ".client-header";


    /**
     * Header name of Message Signature
     *
     * @see #Key$signatureHeader
     */
    private String signatureHeader = "Auth-Signature";
    public static final String Key$signatureHeader = Key + ".signature-header";


    /**
     * Header name of Request Timestamp
     *
     * @see #Key$timestampHeader
     */
    private String timestampHeader = "Auth-Timestamp";
    public static final String Key$timestampHeader = Key + ".timestamp-header";


    /**
     * Header name of Response Body Digest
     *
     * @see #Key$digestHeader
     */
    private String digestHeader = "Auth-Digest";
    public static final String Key$digestHeader = Key + ".digest-header";

    /**
     * 超过此大小则不做Digest，默认5MB
     *
     * @see #Key$digestMax
     */
    private DataSize digestMax = DataSize.ofMegabytes(10);
    public static final String Key$digestMax = Key + ".digest-max";
}

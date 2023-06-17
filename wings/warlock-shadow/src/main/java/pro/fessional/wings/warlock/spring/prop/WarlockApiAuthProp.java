package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;

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
     * no digest over this size, default 5M.
     *
     * @see #Key$digestMax
     */
    private DataSize digestMax = DataSize.ofMegabytes(10);
    public static final String Key$digestMax = Key + ".digest-max";

    /**
     * whether it must be signed, compatible with the old api.
     *
     * @see #Key$mustSignature
     */
    private boolean mustSignature = true;
    public static final String Key$mustSignature = Key + ".must-signature";


    /**
     * if there is both a file and a json,
     * use this name for the json body and submit it as a File.
     *
     * @see #Key$fileJsonBody
     */
    private String fileJsonBody = "FILE_JSON_BODY";
    public static final String Key$fileJsonBody = Key + ".file-json-body";

    /**
     * response of client error
     *
     * @see #Key$errorClient
     */
    private SimpleResponse errorClient = new SimpleResponse();
    public static final String Key$errorClient = Key + ".error-client";

    /**
     * response of signature error
     *
     * @see #Key$errorSignature
     */
    private SimpleResponse errorSignature = new SimpleResponse();
    public static final String Key$errorSignature = Key + ".error-signature";

    /**
     * response of unhandled error
     *
     * @see #Key$errorUnhandled
     */
    private SimpleResponse errorUnhandled = new SimpleResponse();
    public static final String Key$errorUnhandled = Key + ".error-unhandled";
}

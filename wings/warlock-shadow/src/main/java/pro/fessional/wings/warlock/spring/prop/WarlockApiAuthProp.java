package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import pro.fessional.wings.slardar.webmvc.MessageResponse;

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

    /**
     * 是否一定要签名，可兼容旧api
     *
     * @see #Key$mustSignature
     */
    private boolean mustSignature = true;
    public static final String Key$mustSignature = Key + ".must-signature";


    /**
     * 既又文件又有json的时候，以此命名json body作为File提交
     *
     * @see #Key$fileJsonBody
     */
    private String fileJsonBody = "FILE_JSON_BODY";
    public static final String Key$fileJsonBody = Key + ".file-json-body";

    /**
     * @see #Key$errorClient
     */
    private MessageResponse errorClient = new MessageResponse();
    public static final String Key$errorClient = Key + ".error-client";

    /**
     * @see #Key$errorSignature
     */
    private MessageResponse errorSignature = new MessageResponse();
    public static final String Key$errorSignature = Key + ".error-signature";

    /**
     * @see #Key$errorUnhandled
     */
    private MessageResponse errorUnhandled = new MessageResponse();
    public static final String Key$errorUnhandled = Key + ".error-unhandled";
}

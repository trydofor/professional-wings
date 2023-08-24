package pro.fessional.wings.slardar.autozone;

/**
 * <pre>
 * The conversion should occur at the Controller level
 * (1) when request, form string to DateTime, form Client to System, by spring or jackson
 * (2) when response, from DateTime to string, from System to Client, by jackson
 * </pre>
 *
 * @author trydofor
 * @since 2022-10-02
 */
public enum AutoZoneType {
    /**
     * To System timezone
     */
    System,
    /**
     * to Client timezone, should convert at Controller level
     */
    Client,
    /**
     * Auto convert to target timezone by request and response
     */
    Auto,
    /**
     * disable convert
     */
    Off,

    ;

    public static AutoZoneType valueOf(boolean auto) {
        return auto ? Auto : Off;
    }
}

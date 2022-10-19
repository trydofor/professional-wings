package pro.fessional.wings.slardar.security;

import javax.servlet.http.HttpServletRequest;

/**
 * @author trydofor
 * @since 2022-01-18
 */
public class WingsAuthHelper {

    public static final String AuthType = "authType";
    public static final String AuthZone = "authZone";
    public static final String AuthAddr = "AuthAddr";
    public static final String AuthAgent = "AuthAgent";
    public static final String ExtName = "extName";

    private static final String InternalRequestAuthType = WingsAuthHelper.class.getName() + AuthType;
    private static final String InternalRequestAuthZone = WingsAuthHelper.class.getName() + AuthZone;

    public static void setAuthTypeAttribute(HttpServletRequest request, Enum<?> type) {
        request.setAttribute(InternalRequestAuthType, type);
    }

    public static Enum<?> getAuthTypeAttribute(HttpServletRequest request) {
        return (Enum<?>) request.getAttribute(InternalRequestAuthType);
    }

    public static void setAuthZoneAttribute(HttpServletRequest request, String zone) {
        request.setAttribute(InternalRequestAuthZone, zone);
    }

    public static String getAuthZoneAttribute(HttpServletRequest request) {
        return (String) request.getAttribute(InternalRequestAuthZone);
    }
}

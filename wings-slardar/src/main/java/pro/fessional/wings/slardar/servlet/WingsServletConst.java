package pro.fessional.wings.slardar.servlet;

/**
 * @author trydofor
 * @since 2020-09-28
 */
public class WingsServletConst {

    public static int wingsOrder = -10_0_9 * (10 + 7);
    public static final int ORDER_FILTER_OVERLOAD = (wingsOrder++) * 70;
    public static final int ORDER_FILTER_CAPTCHA = (wingsOrder++) * 70;
    public static final int ORDER_FILTER_TERMINAL = (wingsOrder++) * 70;
    public static final int ORDER_FILTER_DOMAINEX = (wingsOrder++) * 70;
    public static final int ORDER_FIRST_BLOOD_IMG = (wingsOrder++) * 70;

    //
    public static final String ATTR_DOMAIN_EXTEND = "WINGS.ATTR.DOMAIN_EXTEND";
    public static final String ATTR_I18N_CONTEXT = "WINGS.ATTR.I18N_CONTEXT";
    public static final String ATTR_REMOTE_IP = "WINGS.ATTR.REMOTE_IP";
    public static final String ATTR_AGENT_INFO = "WINGS.ATTR.AGENT_INFO";
}

package pro.fessional.wings.slardar.servlet;

/**
 * @author trydofor
 * @since 2019-11-19
 */
public final class WingsFilterOrder {
    private WingsFilterOrder() {
    }

    public static final int OVERLOAD = -10_0_9 * (10 + 7);
    public static final int CAPTCHA = OVERLOAD + 10 + 7;
    public static final int OAUTH2X = CAPTCHA + 10 + 7;
}

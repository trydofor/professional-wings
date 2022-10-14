package pro.fessional.wings.slardar.autozone;

/**
 * <pre>
 * 转换在Controller层发生
 * ① request时，从string到DateTime，从CLient到System，spring或jackson完成
 * ② response时，从DateTime到string，从System到Client，jackson完成。
 * </pre>
 *
 * @author trydofor
 * @since 2022-10-02
 */
public enum AutoZoneType {
    /**
     * 转到系统时区
     */
    System,
    /**
     * 转到客户时区，仅限于Controller层
     */
    Client,
    /**
     * 按request和response自动转换到目的地时区
     */
    Auto,
    /**
     * 关闭转换功能
     */
    Off,

    ;

    public static AutoZoneType valueOf(boolean auto) {
        return auto ? Auto : Off;
    }
}

package pro.fessional.wings.slardar.autozone.dto;

/**
 * TODO 目前 jackson和excel兼容性未解决
 * <p>
 * 受Spring IOC管理的，Aspect的，自动转换ZonedDateTime的Dto
 *
 * @author trydofor
 * @since 2021-03-22
 */
public @interface AutoZoneDto {

    enum Zone {
        System,
        User
    }

    /**
     * 自动转换的目标Zone
     *
     * @return 目标Zone
     */
    Zone value() default Zone.User;
}

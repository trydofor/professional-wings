package pro.fessional.wings.warlock.service.auth;

/**
 * @author trydofor
 * @since 2021-02-16
 */
public enum WarlockAuthType {
    /**
     * login by username, generally.
     */
    USERNAME,
    /**
     * use mobile number as username
     */
    MOBILE,
    /**
     * use email as username
     */
    EMAIL,
}

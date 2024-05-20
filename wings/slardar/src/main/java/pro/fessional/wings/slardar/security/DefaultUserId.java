package pro.fessional.wings.slardar.security;

/**
 * <pre>
 * The UserId range in wings convention,
 * a positive number is a login user,
 * otherwise it is a non-login user.
 *
 * (1) Big positive numbers are business users;
 *     small positive numbers are built-in users;
 *     0-99 are reserved Wings users.
 * (2) Negative numbers are special users.
 * (3) Zero has no privileges, root has the highest privileges,
 *     it is recommended not to login.
 * </pre>
 *
 * @author trydofor
 * @since 2021-02-20
 */
public interface DefaultUserId {

    /**
     * Null User
     */
    long Null = Long.MIN_VALUE;

    /**
     * Non-login User
     */
    long Guest = -1;

    /**
     * No privileges User
     */
    long Nobody = 0;

    /**
     * Super User
     */
    long Root = 1;

    /**
     * Daemon User
     */
    long Daemon = 2;

    /**
     * Whether is Guest (eq -1)
     */
    static boolean isGuest(long uid) {
        return uid == Guest;
    }

    /**
     * Whether as Guest (null or le -1)
     */
    static boolean asGuest(Long uid) {
        return uid == null || uid <= Guest;
    }

    /**
     * Whether Null value
     */
    static boolean isNull(long uid) {
        return uid == Null;
    }

    /**
     * Whether null Object or Null value
     */
    static boolean asNull(Long uid) {
        return uid == null || uid == Null;
    }

    /**
     * trim value or Null
     */
    static long value(Long uid){
        return uid == null ? Null : uid;
    }

    /**
     * 0-99 are reserved Wings users.
     */
    long Wings = 99;

    /**
     * Whether reserved Wings users.
     */
    static boolean isWings(long uid) {
        return uid >= Nobody && uid <= Wings;
    }

    /**
     * Whether null or reserved Wings users.
     */
    static boolean asWings(Long uid) {
        return uid == null || (uid >= Nobody && uid <= Wings);
    }

}

package pro.fessional.wings.warlock.service.auth;

/**
 * @author trydofor
 * @since 2023-07-10
 */
public interface WarlockDangerService {
    /**
     * block this login
     *
     * @param authType authn type
     * @param username username
     * @param seconds  username
     */
    void block(Enum<?> authType, String username, int seconds);

    /**
     * check blocking second
     *
     * @param authType authn type
     * @param username username
     * @return zero or negative mean allow
     */
    int check(Enum<?> authType, String username);

    /**
     * allow this login
     *
     * @param authType authn type
     * @param username username
     */
    void allow(Enum<?> authType, String username);
}

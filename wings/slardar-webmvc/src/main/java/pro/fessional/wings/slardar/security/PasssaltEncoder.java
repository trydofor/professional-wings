package pro.fessional.wings.slardar.security;

/**
 * Password salting
 *
 * @author trydofor
 * @since 2021-02-25
 */
public interface PasssaltEncoder {

    /**
     * encode pass with salt
     *
     * @param pass plain password
     * @param salt salt
     * @return new password with salt
     */
    String salt(String pass, String salt);
}

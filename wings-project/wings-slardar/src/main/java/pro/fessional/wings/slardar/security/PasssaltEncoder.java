package pro.fessional.wings.slardar.security;

/**
 * @author trydofor
 * @since 2021-02-25
 */
public interface PasssaltEncoder {

    /**
     * 根据算法组合salt和pass
     *
     * @param pass 原始密码
     * @param salt salt
     * @return 新密码
     */
    String salt(String pass, String salt);
}

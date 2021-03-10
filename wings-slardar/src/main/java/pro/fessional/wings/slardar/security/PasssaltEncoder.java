package pro.fessional.wings.slardar.security;

/**
 * @author trydofor
 * @since 2021-02-25
 */
public interface PasssaltEncoder {

    int MIN_LENGTH = 40;

    /**
     * 生成一个指定长度的salt
     *
     * @param len 小于等于0为不限长度，长度至少40位。
     * @return salt
     */
    String salt(int len);

    /**
     * 根据算法组合salt和pass
     *
     * @param pass 原始密码
     * @param salt salt
     * @return 新密码
     */
    String salt(String pass, String salt);
}

package pro.fessional.wings.slardar.security;

/**
 * wings约定的UserId区间，正数为登录用户，否则为不可登录用户。
 * ①正数，大正数-业务用户；小正数-内置用户；0-99为wings保留用户
 * ②负数，特殊用户。
 * ③零无任何权限，root有最高权限，建议不可登录。
 *
 * @author trydofor
 * @since 2021-02-20
 */
public interface DefaultUserId {

    /**
     * 非登录用户的统称。
     */
    long Unknown = -1;

    /**
     * 无权用户，-2:Unprivileged User
     */
    long Nobody = 0;

    /**
     * 超级用户，0:System Administrator
     */
    long Root = 1;

    /**
     * 守护进程，1:System Services
     */
    long Daemon = 2;
}
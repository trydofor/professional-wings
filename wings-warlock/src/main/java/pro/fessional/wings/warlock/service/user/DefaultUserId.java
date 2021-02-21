package pro.fessional.wings.warlock.service.user;

/**
 * @author trydofor
 * @since 2021-02-20
 */
public interface DefaultUserId {
    /**
     * 无权用户
     */
    long Nobody = 0;

    /**
     * 超级用户
     */
    long Root = 1;

    /**
     * 守护进程
     */
    long Daemon = 2;
}

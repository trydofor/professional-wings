package pro.fessional.wings.faceless.service.lightid;

import javax.validation.constraints.NotNull;

/**
 * @author trydofor
 * @since 2019-05-30
 */
public interface LightIdService {

    /**
     * 获得默认的BlockId
     *
     * @return 默认的BlockId
     */
    default int geBlockId() {
        return 0;
    }

    /**
     * 按Jooq的 pojo命名获得，按 小写_小写命名。
     *
     * @param po jooq的Pojo
     * @return id
     */
    default long getId(@NotNull Class<? extends LightIdAware> po) {
        return getId(po, geBlockId());
    }

    /**
     * 按名字获得id，不区分大小写，默认全小写。
     *
     * @param name 名字
     * @return id
     */
    default long getId(@NotNull String name) {
        return getId(name, geBlockId());
    }

    /**
     * 按Jooq的 pojo命名获得，按 小写_小写命名。
     *
     * @param po    jooq的Pojo
     * @param block 区块
     * @return id
     */
    default long getId(@NotNull Class<? extends LightIdAware> po, int block) {
        String name = po.getSimpleName();
        int len = name.length();
        StringBuilder sb = new StringBuilder(len + 10);
        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                if (i > 0) sb.append('_');
                sb.append((char) (c + 32));
            } else {
                sb.append(c);
            }
        }
        return getId(sb.toString(), block);
    }

    /**
     * 按名字获得id，不区分大小写，默认全小写。
     *
     * @param name  名字
     * @param block 区块
     * @return id
     */
    long getId(@NotNull String name, int block);
}

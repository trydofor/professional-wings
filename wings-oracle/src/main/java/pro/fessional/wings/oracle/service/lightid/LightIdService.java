package pro.fessional.wings.oracle.service.lightid;

import javax.validation.constraints.NotNull;

/**
 * @author trydofor
 * @since 2019-05-30
 */
public interface LightIdService {

    /**
     * 按Jooq的 pojo命名获得，按 大写_大写命名。
     *
     * @param block 区块
     * @param po    jooq的Pojo
     * @return id
     */
    default long getId(int block, @NotNull Class<LightIdAware> po) {
        String name = po.getSimpleName();
        int len = name.length();
        StringBuilder sb = new StringBuilder(len + 10);
        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                if (i > 0) sb.append('_');
                sb.append(c);
            } else if (c >= 'a' && c <= 'z') {
                sb.append((char) (c - 32));
            } else {
                sb.append(c);
            }
        }
        return getId(block, sb.toString());
    }

    /**
     * 按名字获得id，不区分大小写，默认全大写。
     *
     * @param block 区块
     * @param name  名字
     * @return id
     */
    long getId(int block, @NotNull String name);
}

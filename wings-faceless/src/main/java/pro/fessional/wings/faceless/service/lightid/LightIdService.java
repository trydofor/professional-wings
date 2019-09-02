package pro.fessional.wings.faceless.service.lightid;

import javax.validation.constraints.NotNull;

/**
 * @author trydofor
 * @since 2019-05-30
 */
public interface LightIdService {

    /**
     * 按Jooq的 pojo命名获得，按 大写_大写命名。
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
     * 按名字获得id，不区分大小写，默认全大写。
     *
     * @param name  名字
     * @param block 区块
     * @return id
     */
    long getId(@NotNull String name, int block);
}

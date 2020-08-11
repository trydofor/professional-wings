package pro.fessional.wings.faceless.service.lightid;


import org.jetbrains.annotations.NotNull;

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
     * 按Jooq的Table命名获得，去掉结尾的`Table`后缀，按 小写_小写命名。
     *
     * @param table jooq的Table
     * @return id
     */
    default long getId(@NotNull Class<? extends LightIdAware> table) {
        return getId(table, geBlockId());
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
     * 按Jooq的Table命名获得，去掉结尾的`Table`后缀，按 小写_小写命名。
     *
     * @param table jooq的Table
     * @param block 区块
     * @return id
     */
    default long getId(@NotNull Class<? extends LightIdAware> table, int block) {
        String name = table.getSimpleName();
        int len = name.endsWith("Table") ? name.length() - 5 : name.length();
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

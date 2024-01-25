package pro.fessional.wings.faceless.service.lightid.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;

import static pro.fessional.wings.faceless.database.helper.JdbcTemplateHelper.FirstIntegerOrNull;

/**
 * @author trydofor
 * @since 2019-09-12
 */

@RequiredArgsConstructor
public class DefaultBlockIdProvider implements BlockIdProvider {

    private final String sql;
    private final JdbcTemplate template;
    private volatile int blockId = Integer.MIN_VALUE;

    @Override
    public int getBlockId() {
        if (blockId != Integer.MIN_VALUE) {
            return blockId;
        }

        synchronized (this) {
            if (blockId == Integer.MIN_VALUE) {
                blockId = selectBlockId();
            }
        }

        return blockId;
    }

    private int selectBlockId() {
        Integer id = template.query(sql, FirstIntegerOrNull);
        if (id == null) {
            throw new IllegalStateException("can not find blockId by sql=" + sql);
        }
        return id;
    }
}

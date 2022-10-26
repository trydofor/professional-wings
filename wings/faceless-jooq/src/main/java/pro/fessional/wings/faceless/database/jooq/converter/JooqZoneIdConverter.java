package pro.fessional.wings.faceless.database.jooq.converter;

import org.jooq.impl.AbstractConverter;
import pro.fessional.wings.faceless.enums.TimezoneEnumUtil;

import java.time.ZoneId;

/**
 * @author trydofor
 * @since 2021-01-18
 */
public class JooqZoneIdConverter extends AbstractConverter<Integer, ZoneId> {

    public JooqZoneIdConverter() {
        super(Integer.class, ZoneId.class);
    }

    @Override
    public ZoneId from(Integer zid) {
        return TimezoneEnumUtil.zoneIdOrNull(zid);
    }

    @Override
    public Integer to(ZoneId zid) {
        return TimezoneEnumUtil.zoneIdOrThrow(zid);
    }
}

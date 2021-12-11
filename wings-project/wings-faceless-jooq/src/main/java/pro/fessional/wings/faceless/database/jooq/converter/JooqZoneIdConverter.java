package pro.fessional.wings.faceless.database.jooq.converter;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import pro.fessional.wings.faceless.enums.TimezoneEnumUtil;

import java.time.ZoneId;

/**
 * @author trydofor
 * @since 2021-01-18
 */
public class JooqZoneIdConverter implements Converter<Integer, ZoneId> {

    @Override
    public ZoneId from(Integer zid) {
        return TimezoneEnumUtil.zoneIdOrNull(zid);
    }

    @Override
    public Integer to(ZoneId zid) {
        return TimezoneEnumUtil.zoneIdOrThrow(zid);
    }

    @Override
    public @NotNull Class<Integer> fromType() {
        return Integer.class;
    }

    @Override
    public @NotNull Class<ZoneId> toType() {
        return ZoneId.class;
    }
}

package pro.fessional.wings.faceless.database.jooq.converter;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import pro.fessional.mirana.i18n.ZoneIdResolver;

import java.time.ZoneId;

/**
 * @author trydofor
 * @since 2021-01-18
 */
public class JooqZoneStrConverter implements Converter<String, ZoneId> {

    @Override
    public ZoneId from(String zid) {
        return ZoneIdResolver.zoneId(zid);
    }

    @Override
    public String to(ZoneId zid) {
        return zid.getId();
    }

    @Override
    public @NotNull Class<String> fromType() {
        return String.class;
    }

    @Override
    public @NotNull Class<ZoneId> toType() {
        return ZoneId.class;
    }
}

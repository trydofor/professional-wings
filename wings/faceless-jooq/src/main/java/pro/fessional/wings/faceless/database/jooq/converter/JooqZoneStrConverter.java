package pro.fessional.wings.faceless.database.jooq.converter;

import org.jooq.impl.AbstractConverter;
import pro.fessional.mirana.i18n.ZoneIdResolver;

import java.time.ZoneId;

/**
 * @author trydofor
 * @since 2021-01-18
 */
public class JooqZoneStrConverter extends AbstractConverter<String, ZoneId> {

    public JooqZoneStrConverter() {
        super(String.class, ZoneId.class);
    }

    @Override
    public ZoneId from(String zid) {
        return ZoneIdResolver.zoneId(zid);
    }

    @Override
    public String to(ZoneId zid) {
        return zid.getId();
    }
}

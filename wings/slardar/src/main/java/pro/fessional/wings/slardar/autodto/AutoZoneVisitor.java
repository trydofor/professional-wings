package pro.fessional.wings.slardar.autodto;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.anti.BeanVisitor;
import pro.fessional.wings.slardar.autozone.AutoTimeZone;
import pro.fessional.wings.slardar.autozone.AutoZoneType;
import pro.fessional.wings.slardar.autozone.AutoZoneUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

/**
 * @author trydofor
 * @since 2022-10-05
 */
@RequiredArgsConstructor
public class AutoZoneVisitor extends BeanVisitor.ContainerVisitor {

    private final Supplier<ZoneId> clientZoneSupplier;
    private final boolean isRequest;

    @Override
    public boolean cares(@NotNull Field field, @NotNull Annotation[] annos) {
        for (Annotation an : annos) {
            if (AutoTimeZone.class.equals(an.annotationType())) {
                return ((AutoTimeZone) an).value() != AutoZoneType.Off;
            }
        }
        return false;
    }

    @Override
    @Nullable
    protected Object amendValue(@NotNull Field field, @NotNull Annotation[] annos, @Nullable Object obj) {
        AutoZoneType autoType = AutoZoneType.Off;
        for (Annotation an : annos) {
            if (AutoTimeZone.class.equals(an.annotationType())) {
                autoType = ((AutoTimeZone) an).value();
                break;
            }
        }

        if (autoType == AutoZoneType.Off) return obj;

        if (obj instanceof LocalDateTime) {
            if (isRequest) {
                return AutoZoneUtil.autoLocalRequest((LocalDateTime) obj, autoType, clientZoneSupplier);
            }
            else {
                return AutoZoneUtil.autoLocalResponse((LocalDateTime) obj, autoType, clientZoneSupplier);
            }
        }

        if (obj instanceof ZonedDateTime) {
            if (isRequest) {
                return AutoZoneUtil.autoZonedRequest((ZonedDateTime) obj, autoType, clientZoneSupplier);
            }
            else {
                return AutoZoneUtil.autoZonedResponse((ZonedDateTime) obj, autoType, clientZoneSupplier);
            }
        }

        if (obj instanceof OffsetDateTime) {
            if (isRequest) {
                return AutoZoneUtil.autoOffsetRequest((OffsetDateTime) obj, autoType, clientZoneSupplier);
            }
            else {
                return AutoZoneUtil.autoOffsetResponse((OffsetDateTime) obj, autoType, clientZoneSupplier);
            }
        }

        return obj;
    }
}

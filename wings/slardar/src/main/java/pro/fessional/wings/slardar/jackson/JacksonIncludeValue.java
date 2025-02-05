package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 *
 * @author trydofor
 * @see JsonInclude
 * @since 2021-10-28
 */
@Slf4j
public class JacksonIncludeValue {

    protected JacksonIncludeValue(@Nullable LocalDate emptyDate, int offset) {
        EmptyDate = emptyDate == null ? null : new EmptyDateRange(emptyDate, offset);
    }

    private static volatile EmptyDateRange EmptyDate;

    @SuppressWarnings({ "EqualsWhichDoesntCheckParameterClass", "EqualsDoesntCheckParameterClass" })
    public static class EmptyDates {
        @Override
        public boolean equals(Object obj) {
            if (obj == null) return true;

            final EmptyDateRange empty = EmptyDate;
            if (empty == null) return false;

            return switch (obj) {
                case Date dt -> empty.isEmpty(dt);
                case LocalDate dt -> empty.isEmpty(dt);
                case LocalDateTime dt -> empty.isEmpty(dt);
                case ZonedDateTime dt -> empty.isEmpty(dt);
                case OffsetDateTime dt -> empty.isEmpty(dt);
                default -> false;
            };
        }
    }

    public static final Value NonEmptyValue = Value.construct(Include.NON_EMPTY, null, null, null);
    public static final Value NonEmptyDates = Value.construct(Include.CUSTOM, null, EmptyDates.class, null);

    /*
     * com.fasterxml.jackson.databind.ser.std.StdArraySerializers
     * BasicSerializerFactory#buildArraySerializer
     */
    public static void configNonEmptyDates(@NotNull ObjectMapper mapper) {
        config(mapper, NonEmptyDates,
            java.sql.Date.class,
            Date.class,
            LocalDate.class,
            LocalDateTime.class,
            ZonedDateTime.class,
            OffsetDateTime.class
            );
    }

    public static void configNonEmptyValue(@NotNull ObjectMapper mapper, Class<?>... claz) {
        config(mapper, NonEmptyValue, claz);
    }

    public static void config(@NotNull ObjectMapper mapper, @NotNull Value value, Class<?>... claz) {
        for (Class<?> clz : claz) {
            mapper.configOverride(clz).setInclude(value);
        }
    }

    private static class EmptyDateRange {
        private final @NotNull LocalDate emptyDate;
        private final LocalDateTime emptyDateMin;
        private final LocalDateTime emptyDateMax;
        private final long timestampMin;
        private final long timestampMax;

        public EmptyDateRange(@NotNull LocalDate empty, int offsetHours) {
            emptyDate = empty;

            long offsetMs = offsetHours * 3600_000L;
            final LocalDateTime dt = empty.atStartOfDay();
            final long ms = dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            timestampMin = ms - offsetMs;
            timestampMax = ms + offsetMs;
            if (offsetHours == 0) {
                emptyDateMin = null;
                emptyDateMax = null;
            }
            else {
                emptyDateMin = dt.minusHours(offsetHours);
                emptyDateMax = dt.plusHours(offsetHours);

            }
        }

        public boolean isEmpty(Date dt) {
            if (dt == null) return true;

            final long time = dt.getTime();
            return timestampMin <= time && time <= timestampMax;
        }

        public boolean isEmpty(LocalDate dt) {
            if (dt == null) return true;

            return emptyDate.equals(dt);
        }

        public boolean isEmpty(LocalDateTime dt) {
            if (dt == null) return true;
            return _isEmpty(dt);
        }

        public boolean isEmpty(ZonedDateTime dt) {
            if (dt == null) return true;
            return _isEmpty(dt.toLocalDateTime());
        }

        public boolean isEmpty(OffsetDateTime dt) {
            if (dt == null) return true;
            return _isEmpty(dt.toLocalDateTime());
        }

        // Considering timezone, the difference is considered equal within `offset` hours.
        private boolean _isEmpty(@NotNull LocalDateTime dt) {
            if (emptyDate.equals(dt.toLocalDate())) return true;

            if (emptyDateMin == null || emptyDateMax == null) {
                return false;
            }
            else {
                //noinspection RedundantCompareToJavaTime
                return emptyDateMin.compareTo(dt) <= 0 && dt.compareTo(emptyDateMax) <= 0;
            }
        }
    }
}

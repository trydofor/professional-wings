package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.time.ThreadNow;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * <pre>
 * no output if LocalDate, LocalDateTime, ZonedDateTime or OffsetDateTime is `empty`.
 * no output if Array, Collection or Map is `empty`.
 * </pre>
 *
 * @author trydofor
 * @since 2021-10-28
 */
@Slf4j
public class EmptyValuePropertyFilter implements AutoRegisterPropertyFilter {

    public static final String Id = "EmptyValue";

    @JsonFilter(Id)
    public static class EmptyDateMixin {
    }

    private final LocalDate emptyDate;
    private final LocalDateTime emptyDateMin;
    private final LocalDateTime emptyDateMax;
    private final boolean emptyList;
    private final boolean emptyMap;

    public EmptyValuePropertyFilter(@Nullable LocalDate emptyDate, int offset, boolean list, boolean map) {
        this.emptyDate = emptyDate;
        if (emptyDate != null) {
            final LocalDateTime dt = emptyDate.atStartOfDay();
            if (offset != 0) {
                this.emptyDateMin = dt.minusHours(offset);
                this.emptyDateMax = dt.plusHours(offset);
            }
            else {
                this.emptyDateMin = null;
                this.emptyDateMax = null;
            }
        }
        else {
            this.emptyDateMin = null;
            this.emptyDateMax = null;
        }
        this.emptyList = list;
        this.emptyMap = map;
    }

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator gen, SerializerProvider prov, PropertyWriter writer) throws Exception {
        final JavaType rt = writer.getType();
        if (writer instanceof BeanPropertyWriter) {
            try {
                BeanPropertyWriter wt = (BeanPropertyWriter) writer;
                if ((emptyDate != null && dealEmptyDate(pojo, gen, prov, wt, rt)) ||
                    (emptyList && dealEmptyList(pojo, gen, prov, wt, rt)) ||
                    (emptyMap && dealEmptyMap(pojo, gen, prov, wt, rt))) {
                    return;
                }
            }
            catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping '" + writer.getFullName() + "' on '" + pojo.getClass().getName()
                              + "' as an exception was thrown when retrieving its value", ex);
                }
            }
        }
        writer.serializeAsField(pojo, gen, prov);
    }

    private boolean dealEmptyList(Object pojo, JsonGenerator gen, SerializerProvider prov, BeanPropertyWriter writer, JavaType rt) throws Exception {
        final boolean isArr = rt.isArrayType();
        if ((isArr || rt.isTypeOrSubTypeOf(Collection.class))) {
            final Object v = writer.get(pojo);
            if ((v instanceof Collection && ((Collection<?>) v).isEmpty()) ||
                (isArr && v != null && Array.getLength(v) == 0)) {
                return skipEmpty(pojo, gen, prov, writer);
            }
        }

        return false;
    }

    private boolean dealEmptyMap(Object pojo, JsonGenerator gen, SerializerProvider prov, BeanPropertyWriter writer, JavaType rt) throws Exception {
        if (rt.isTypeOrSubTypeOf(Map.class)) {
            final Object v = writer.get(pojo);
            if (v instanceof Map && ((Map<?, ?>) v).isEmpty()) {
                return skipEmpty(pojo, gen, prov, writer);
            }
        }
        return false;
    }

    private boolean dealEmptyDate(Object pojo, JsonGenerator gen, SerializerProvider prov, BeanPropertyWriter writer, JavaType rt) throws Exception {
        if ((rt.isTypeOrSubTypeOf(LocalDate.class) ||
             rt.isTypeOrSubTypeOf(LocalDateTime.class) ||
             rt.isTypeOrSubTypeOf(ZonedDateTime.class) ||
             rt.isTypeOrSubTypeOf(OffsetDateTime.class) ||
             rt.isTypeOrSubTypeOf(Date.class))
        ) {
            final Object v = writer.get(pojo);
            if ((v instanceof LocalDate && emptyDate((LocalDate) v)) ||
                (v instanceof LocalDateTime && emptyDateTime((LocalDateTime) v)) ||
                (v instanceof ZonedDateTime && emptyDateTime(((ZonedDateTime) v).toLocalDateTime())) ||
                (v instanceof OffsetDateTime && emptyDateTime(((OffsetDateTime) v).toLocalDateTime())) ||
                (v instanceof Date && emptyDateTime((Date) v))
            ) {
                return skipEmpty(pojo, gen, prov, writer);
            }
        }
        return false;
    }

    private boolean emptyDate(LocalDate d) {
        return emptyDate.equals(d);
    }

    private boolean emptyDateTime(Date dt) {
        final LocalDateTime ldt = dt.toInstant().atZone(ThreadNow.sysZoneId()).toLocalDateTime();
        return emptyDateTime(ldt);
    }

    // Considering timezone, the difference is considered equal within `offset` hours.
    private boolean emptyDateTime(LocalDateTime dt) {
        if (emptyDate.equals(dt.toLocalDate())) return true;
        if (emptyDateMin == null || emptyDateMax == null) {
            return false;
        }
        else {
            return !dt.isBefore(emptyDateMin) && !dt.isAfter(emptyDateMax);
        }
    }

    private boolean skipEmpty(Object pojo, JsonGenerator gen, SerializerProvider prov, BeanPropertyWriter writer) throws Exception {
        if (!gen.canOmitFields()) {
            writer.serializeAsOmittedField(pojo, gen, prov);
        }
        return true;
    }
}

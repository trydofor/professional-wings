package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * @author trydofor
 * @since 2021-10-28
 */
@Slf4j
@RequiredArgsConstructor
public class EmptyDatePropertyFilter implements AutoRegisterPropertyFilter {

    public static final String Id = "EmptyDate";

    @JsonFilter(Id)
    public static class EmptyDateMixin {
    }

    private final LocalDate emptyDate;

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator gen, SerializerProvider prov, PropertyWriter writer) throws Exception {
        final JavaType rt = writer.getType();
        if ((rt.isTypeOrSubTypeOf(LocalDate.class) ||
             rt.isTypeOrSubTypeOf(LocalDateTime.class) ||
             rt.isTypeOrSubTypeOf(ZonedDateTime.class) ||
             rt.isTypeOrSubTypeOf(OffsetDateTime.class))
            && writer instanceof BeanPropertyWriter
        ) {
            try {
                final Object v = ((BeanPropertyWriter) writer).get(pojo);
                if ((v instanceof LocalDate && emptyDate.equals(v)) ||
                    (v instanceof LocalDateTime && emptyDate.equals(((LocalDateTime) v).toLocalDate())) ||
                    (v instanceof ZonedDateTime && emptyDate.equals(((ZonedDateTime) v).toLocalDate())) ||
                    (v instanceof OffsetDateTime && emptyDate.equals(((OffsetDateTime) v).toLocalDate()))
                ) {
                    if (!gen.canOmitFields()) {
                        writer.serializeAsOmittedField(pojo, gen, prov);
                    }
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
}

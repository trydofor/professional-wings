package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.ANY;

/**
 * Directly use JsonFormat and DecimalFormat to format numbers
 *
 * @author trydofor
 * @since 2021-07-06
 */
public class FormatNumberSerializer extends NumberSerializer {

    public enum Digital {
        Auto,
        True,
        False,
    }

    private static final long MIN_SAFE_INTEGER = -9007199254740991L;
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;

    private final DecimalFormat format;
    private final Digital digital;
    private final Map<String, DecimalFormat> poolsAuto = new ConcurrentHashMap<>();
    private final Map<String, DecimalFormat> poolsNoop = new ConcurrentHashMap<>();

    public FormatNumberSerializer(Class<? extends Number> rawType, DecimalFormat format, Digital digital) {
        super(rawType);
        this.format = format;
        this.digital = digital;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (format == null) {
            return super.createContextual(prov, property);
        }
        JsonFormat.Value jf = findFormatOverrides(prov, property, handledType());
        final String ptn = jf == null ? null : jf.getPattern();
        if (StringUtils.hasLength(ptn)) {
            final DecimalFormat df;
            if (jf.getShape() == ANY) {
                df = poolsAuto.computeIfAbsent(ptn, k -> {
                    DecimalFormat d = new DecimalFormat(ptn);
                    d.setRoundingMode(format.getRoundingMode());
                    d.setDecimalFormatSymbols(format.getDecimalFormatSymbols());
                    return d;
                });
            }
            else {
                df = poolsNoop.computeIfAbsent(ptn, k -> {
                    DecimalFormat d = new DecimalFormat(ptn);
                    d.setRoundingMode(format.getRoundingMode());
                    return d;
                });
            }

            return new FormatNumberSerializer(_handledType, df, digital);
        }
        return this;
    }

    @Override
    public void serialize(Number value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (format != null || digital != Digital.False
            || value instanceof Long || value instanceof Integer
            || value instanceof Float || value instanceof Double
            || value instanceof BigDecimal || value instanceof BigInteger) {
            final String str = format == null ? String.valueOf(value) : this.format.format(value);
            if (digital == Digital.True) {
                g.writeRawValue(str);
            }
            else if (digital == Digital.Auto) {
                final long vl = value.longValue();
                if (vl <= MIN_SAFE_INTEGER || vl >= MAX_SAFE_INTEGER) {
                    g.writeNumber(str);
                }
                else {
                    g.writeRawValue(str);
                }
            }
            else {
                g.writeNumber(str);
            }
        }
        else {
            super.serialize(value, g, provider);
        }
    }
}

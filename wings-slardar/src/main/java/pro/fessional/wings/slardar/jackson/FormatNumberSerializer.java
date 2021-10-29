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
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 直接JsonFormat和DecimalFormat来格式化数字
 *
 * @author trydofor
 * @since 2021-07-06
 */
public class FormatNumberSerializer extends NumberSerializer {

    private final DecimalFormat format;
    private final boolean digital;
    private final Map<String, DecimalFormat> pools = new ConcurrentHashMap<>();

    /**
     * @param rawType 类型
     * @since 2.5
     */
    public FormatNumberSerializer(Class<? extends Number> rawType, DecimalFormat format, boolean digital) {
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
            DecimalFormat df = pools.computeIfAbsent(ptn, k -> {
                DecimalFormat d = new DecimalFormat(ptn);
                d.setDecimalFormatSymbols(format.getDecimalFormatSymbols());
                d.setRoundingMode(format.getRoundingMode());
                return d;
            });
            return new FormatNumberSerializer(_handledType, df, digital);
        }
        return this;
    }

    @Override
    public void serialize(Number value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (format != null || digital
            || value instanceof Long || value instanceof Integer
            || value instanceof Float || value instanceof Double
            || value instanceof BigDecimal) {
            final String str = format == null ? String.valueOf(value) : this.format.format(value);
            if (digital) {
                g.writeRawValue(str);
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

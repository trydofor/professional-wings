package pro.fessional.wings.slardar.fastjson;

import com.alibaba.fastjson2.annotation.JSONField;
import com.alibaba.fastjson2.filter.ContextValueFilter;
import com.alibaba.fastjson2.filter.ValueFilter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * <a href="https://github.com/alibaba/fastjson2/blob/0c2d4a8e83879e990a990ffaf57bf2e5a8d6f723/docs/fastjson_1_upgrade_cn.md">fastjson_1_upgrade_cn</a>
 *
 * @author trydofor
 * @since 2022-10-25
 */
public class FastJsonFilters {

    /**
     * Number types are all output as String.
     * Note: JSONField.format is supported as DecimalFormat for serialization, but not for deserialization.
     */
    public static final ValueFilter NumberAsString = (object, name, value) -> {
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toPlainString();
        }
        else if (value instanceof Number) {
            return value.toString();
        }

        return value;
    };

    /**
     * Number types are all output as String.
     * Note: JSONField.format is supported as DecimalFormat for serialization, but not for deserialization.
     */
    public static final ContextValueFilter NumberFormatString = (context, object, name, value) -> {
        final JSONField anno = context.getAnnotation(JSONField.class);
        if (anno != null) {
            final String fmt = anno.format();
            if (fmt != null && !fmt.isEmpty()) {
                if (value instanceof Number) {
                    DecimalFormat nf = new DecimalFormat(fmt);
                    return nf.format(value);
                }
            }
        }
        //
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toPlainString();
        }
        else if (value instanceof Number) {
            return value.toString();
        }

        return value;
    };
}

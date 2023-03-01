package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.mirana.bits.Aes;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.silencer.encrypt.Aes256Provider;

import java.io.IOException;

import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.MaskingValue;


/**
 * @author trydofor
 * @since 2022-07-15
 */
public class AesStringSerializer extends JsonSerializer<String> implements ContextualSerializer {

    @Setter(onMethod_ = {@Autowired})
    private Aes aes;
    @NotNull
    private String mis = MaskingValue;

    @Override
    public void serialize(String value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        final String str = aes == null ? mis : aes.encode64(value);
        generator.writeString(str);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        final AesString anno = property.getAnnotation(AesString.class);
        if (anno == null) return this;

        final AesString.Misfire mf = anno.misfire();
        final Aes aes256 = Aes256Provider.get(anno.value(), false);
        if (aes256 == null && mf == AesString.Misfire.Error) {
            throw new IllegalArgumentException("Aes key not found, name=" + anno.value());
        }

        AesStringSerializer ser = new AesStringSerializer();
        ser.aes = aes256;

        if (mf == AesString.Misfire.Masks) {
            ser.mis = MaskingValue;
        }
        else if (mf == AesString.Misfire.Empty) {
            ser.mis = Null.Str;
        }

        return ser;
    }
}

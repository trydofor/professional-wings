package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.mirana.bits.Aes;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.silencer.encrypt.Aes256Provider;
import pro.fessional.wings.slardar.jackson.AesString.Misfire;

import java.io.IOException;

import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.MaskingValue;

/**
 * @author trydofor
 * @since 2022-07-15
 */
public class AesStringDeserializer extends JsonDeserializer<String> implements ContextualDeserializer {

    @Setter(onMethod_ = {@Autowired})
    private Aes aes;
    @NotNull
    private String mis = MaskingValue;

    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getValueAsString();
        return aes == null ? mis : aes.decode64(value);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        final AesString anno = property.getAnnotation(AesString.class);
        if (anno == null) return this;

        final Misfire mf = anno.misfire();
        final Aes aes256 = Aes256Provider.get(anno.value(), false);
        if (aes256 == null && mf == Misfire.Error) {
            throw new IllegalArgumentException("Aes key not found, name=" + anno.value());
        }

        AesStringDeserializer ser = new AesStringDeserializer();
        ser.aes = aes256;

        if (mf == Misfire.Masks) {
            ser.mis = MaskingValue;
        }
        else if (mf == Misfire.Empty) {
            ser.mis = Null.Str;
        }

        return ser;
    }
}

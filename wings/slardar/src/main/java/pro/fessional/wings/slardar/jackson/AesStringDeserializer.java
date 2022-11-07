package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.mirana.bits.Aes;

import java.io.IOException;

/**
 * @author trydofor
 * @since 2022-07-15
 */
public class AesStringDeserializer extends JsonDeserializer<String> {

    @Setter(onMethod_ = {@Autowired})
    private Aes aes;

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        return aes.decode64(value);
    }
}

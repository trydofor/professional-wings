package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.mirana.bits.Aes;

import java.io.IOException;

/**
 * @author trydofor
 * @since 2022-07-15
 */
public class AesStringSerializer extends JsonSerializer<String> {

    @Setter(onMethod_ = {@Autowired})
    private Aes aes;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(aes.encode64(value));
    }
}

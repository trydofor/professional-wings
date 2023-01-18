package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.core.io.Resource;
import pro.fessional.wings.silencer.spring.help.CommonPropHelper;

import java.io.IOException;

/**
 * @author trydofor
 * @since 2023-01-13
 */
public class ResourceSerializer extends JsonSerializer<Resource> {
    @Override
    public void serialize(Resource value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final String res = CommonPropHelper.toString(value);
        gen.writeString(res);
    }
}

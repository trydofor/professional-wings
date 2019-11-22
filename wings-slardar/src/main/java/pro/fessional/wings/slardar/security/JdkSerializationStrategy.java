package pro.fessional.wings.slardar.security;

import org.springframework.core.serializer.support.SerializationFailedException;
import org.springframework.security.oauth2.provider.token.store.redis.StandardStringSerializationStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * copy from spring JdkSerializationStrategy for it's LIMIT
 *
 * @author trydofor
 * @since 2019-11-21
 */
public class JdkSerializationStrategy extends StandardStringSerializationStrategy {

    private static final byte[] NULL = new byte[0];

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T deserializeInternal(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (T) ois.readObject();
        } catch (Exception e) {
            throw new SerializationFailedException("Failed to deserialize payload", e);
        }
    }

    @Override
    protected byte[] serializeInternal(Object object) {
        if (object == null) {
            return NULL;
        }
        if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " requires a Serializable payload but received an object of type [" + object.getClass().getName() + "]");
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new SerializationFailedException("Failed to serialize object", e);
        }
    }
}
package pro.fessional.wings.slardar.security.impl;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.slardar.security.WingsAuthDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-01-18
 */
@EqualsAndHashCode
public class DefaultWingsAuthDetails implements WingsAuthDetails, Serializable {

    @Serial private static final long serialVersionUID = 19791023L;

    private final Map<String, String> metaData = new HashMap<>();
    private Object realData;

    public DefaultWingsAuthDetails() {
    }

    public DefaultWingsAuthDetails(Object data) {
        this.realData = data;
    }

    @Override
    @NotNull
    public Map<String, String> getMetaData() {
        return metaData;
    }

    @Override
    @Nullable
    public Object getRealData() {
        return realData;
    }

    public void setRealData(Object realData) {
        this.realData = realData;
    }
}

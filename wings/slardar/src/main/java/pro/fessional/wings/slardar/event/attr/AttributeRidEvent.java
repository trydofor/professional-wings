package pro.fessional.wings.slardar.event.attr;

import lombok.Data;
import org.jetbrains.annotations.Contract;
import pro.fessional.mirana.best.TypedReg;
import pro.fessional.wings.slardar.context.AttributeHolder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-03-07
 */
@Data
public class AttributeRidEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 6313180145649074244L;

    private final Map<String, Set<Object>> typedReg = new HashMap<>();

    public AttributeRidEvent() {
    }

    public AttributeRidEvent(TypedReg<?, ?> reg, Object... key) {
        Set<Object> keys = typedReg.computeIfAbsent(reg.serialize(), k -> new HashSet<>());
        keys.addAll(Arrays.asList(key));
    }

    @SafeVarargs
    @Contract("_,_->this")
    public final <K> AttributeRidEvent rid(TypedReg<K, ?> reg, K... key) {
        rid(reg, Arrays.asList(key));
        return this;
    }

    @Contract("_,_->this")
    public <K> AttributeRidEvent rid(TypedReg<K, ?> reg, Collection<K> key) {
        Set<Object> keys = typedReg.computeIfAbsent(reg.serialize(), k -> new HashSet<>());
        keys.addAll(key);
        return this;
    }

    public void handleEvent() {
        for (Map.Entry<String, Set<Object>> ent : typedReg.entrySet()) {
            String s = ent.getKey();
            TypedReg<Object, Object> reg = TypedReg.deserialize(s);
            Set<Object> ks = ent.getValue();
            if (ks == null || ks.isEmpty()) {
                AttributeHolder.ridAttrAll(reg);
            }
            else {
                AttributeHolder.ridAttrs(reg, ks);
            }
        }
    }
}

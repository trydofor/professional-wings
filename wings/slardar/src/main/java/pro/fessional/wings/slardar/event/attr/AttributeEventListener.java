package pro.fessional.wings.slardar.event.attr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pro.fessional.mirana.best.TypedReg;
import pro.fessional.wings.slardar.context.AttributeCache;

import java.util.Map;
import java.util.Set;

/**
 * Listener AttributeRidEvent to rid attributes on AttributeCache
 *
 * @author trydofor
 * @since 2023-10-31
 */
@Slf4j
public class AttributeEventListener {

    @EventListener
    public void ridAttr(AttributeRidEvent event) {
        for (Map.Entry<String, Set<Object>> ent : event.getTypedReg().entrySet()) {
            TypedReg<Object, Object> reg = TypedReg.deserialize(ent.getKey(), false);
            if (reg == null) continue;

            final Set<Object> ks = ent.getValue();
            if (ks == null || ks.isEmpty()) {
                log.debug("ridAttrAll, type={}", reg);
                AttributeCache.forEach(reg, AttributeCache::ridAttrAll);
            }
            else {
                log.debug("ridAttrAll, type={}, key-size={}", reg, ks.size());
                AttributeCache.forEach(reg, cache -> cache.ridAttrs(ks));
            }
        }
    }
}

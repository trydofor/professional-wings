package pro.fessional.wings.slardar.event.attr;

import org.springframework.context.event.EventListener;

/**
 * @author trydofor
 * @since 2023-10-31
 */
public class AttributeEventListener {

    @EventListener
    public void ridAttr(AttributeRidEvent event) {
        event.handleEvent();
    }
}

package pro.fessional.wings.silencer.message;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;

/**
 * @author trydofor
 * @since 2023-11-24
 */
public class MessageSourceHelper {

    private static MessageSource messageSource;
    private static CombinableMessageSource combinableMessageSource;

    protected MessageSourceHelper(@NotNull MessageSource primary) {

        if (primary instanceof HierarchicalMessageSource hierarchy) {
            CombinableMessageSource combinable = new CombinableMessageSource();
            MessageSource parent = hierarchy.getParentMessageSource();
            if (parent != null) {
                combinable.setParentMessageSource(parent);
            }
            hierarchy.setParentMessageSource(combinable);
            combinableMessageSource = combinable;
        }

        messageSource = primary;
    }


    @Contract("true->!null")
    public static MessageSource getMessageSource(boolean nonnull) {
        if (nonnull && messageSource == null) throw new IllegalStateException("init before using");
        return messageSource;
    }

    @Contract("true->!null")
    public static CombinableMessageSource getCombinableMessageSource(boolean nonnull) {
        if (nonnull && combinableMessageSource == null) throw new IllegalStateException("init before using");
        return combinableMessageSource;
    }
}

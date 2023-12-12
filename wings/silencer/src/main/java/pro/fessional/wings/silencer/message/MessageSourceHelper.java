package pro.fessional.wings.silencer.message;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2023-11-24
 */
public class MessageSourceHelper {

    public static final CombinableMessageSource Combine = new CombinableMessageSource();
    public static volatile MessageSource Primary = new MessageSource() {
        @Override
        public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
            throw new IllegalStateException("should bind before using");
        }

        @Override
        public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
            throw new IllegalStateException("should bind before using");
        }

        @Override
        public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
            throw new IllegalStateException("should bind before using");
        }
    };

    public static volatile boolean hasPrimary = false;
    public static volatile boolean hasCombine = false;

    protected MessageSourceHelper(@NotNull MessageSource primary) {
        synchronized (MessageSourceHelper.class) {
            if (primary instanceof HierarchicalMessageSource hierarchy) {
                MessageSource parent = hierarchy.getParentMessageSource();
                if (parent != null) {
                    Combine.setParentMessageSource(parent);
                }
                hierarchy.setParentMessageSource(Combine);
                hasCombine = true;
            }

            Primary = primary;
            hasPrimary = true;
        }
    }
}

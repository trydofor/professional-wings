package pro.fessional.wings.silencer.message;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles itself first, then handles [brother]  it, then the parent
 *
 * @author trydofor
 * @since 2019-09-15
 */
public class CombinableMessageSource extends AbstractMessageSource {

    private final ArrayList<OrderedMessageSource> orderedBrotherSources = new ArrayList<>(16);
    private final ConcurrentHashMap<String, ConcurrentHashMap<Locale, String>> codeLocaleString = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<Locale, MessageFormat>> codeLocaleFormat = new ConcurrentHashMap<>();

    @Override
    @Nullable
    protected String resolveCodeWithoutArguments(@NotNull String code, @NotNull Locale locale) {
        ConcurrentHashMap<Locale, String> cache = codeLocaleString.get(code);
        return cache == null ? null : cache.get(locale);
    }

    @Override
    @Nullable
    protected MessageFormat resolveCode(@NotNull String code, @NotNull Locale locale) {
        ConcurrentHashMap<Locale, MessageFormat> cache = codeLocaleFormat.get(code);
        return cache == null ? null : cache.get(locale);
    }

    public void removeMessage(String code) {
        removeMessage(code, null);
    }

    public void removeMessage(String code, Locale locale) {
        if (code == null) return;

        if (locale == null) {
            codeLocaleString.remove(code);
            codeLocaleFormat.remove(code);
        }
        else {
            remove(codeLocaleString, code, locale);
            remove(codeLocaleFormat, code, locale);
        }
    }

    public void addMessage(String code, Locale locale, String msg) {
        Assert.notNull(code, "Code must not be null");
        Assert.notNull(locale, "Locale must not be null");
        Assert.notNull(msg, "Message must not be null");

        codeLocaleString.computeIfAbsent(code, key -> new ConcurrentHashMap<>())
                        .putIfAbsent(locale, msg);

        codeLocaleFormat.computeIfAbsent(code, key -> new ConcurrentHashMap<>())
                        .computeIfAbsent(locale, key -> createMessageFormat(msg, key));

        if (logger.isDebugEnabled()) {
            logger.debug("Added message [" + msg + "] for code [" + code + "] and Locale [" + locale + "]");
        }
    }

    public void addMessage(Map<?, ?> messages, Locale locale) {
        Assert.notNull(messages, "Messages Map must not be null");
        for (Map.Entry<?, ?> en : messages.entrySet()) {
            Object key = en.getKey();
            Object value = en.getValue();
            if (key != null && value != null) {
                addMessage(key.toString(), locale, value.toString());
            }
        }
    }

    @Override
    public void setParentMessageSource(@Nullable MessageSource parent) {
        synchronized (orderedBrotherSources) {
            final int size = orderedBrotherSources.size();
            if (size == 0) {
                super.setParentMessageSource(parent);
            }
            else {
                orderedBrotherSources.get(size - 1).source.setParentMessageSource(parent);
            }
        }
    }

    /**
     * Combine other messageSource in order, and the one with the smaller order number will be resolved first.
     * If order equals Integer.MIN_VALUE, it means that the message will be removed.
     *
     * @param messageSource other messageSource
     * @param order         order Integer#MIN_VALUE means remove
     */
    public void addMessage(HierarchicalMessageSource messageSource, int order) {
        Assert.notNull(messageSource, "messageSource must not be null");
        synchronized (orderedBrotherSources) {
            final int size = orderedBrotherSources.size();
            MessageSource thatParent;
            if (size == 0) {
                thatParent = getParentMessageSource();
            }
            else {
                thatParent = orderedBrotherSources.get(size - 1).source.getParentMessageSource();
            }

            orderedBrotherSources.removeIf(o -> o.source == messageSource);
            if (order != Integer.MIN_VALUE) {
                orderedBrotherSources.add(new OrderedMessageSource(messageSource, order));
                orderedBrotherSources.sort(Comparator.comparingInt(o -> o.order));
            }

            for (int i = orderedBrotherSources.size() - 1; i >= 0; i--) {
                HierarchicalMessageSource source = orderedBrotherSources.get(i).source;
                source.setParentMessageSource(thatParent);
                thatParent = source;
            }

            super.setParentMessageSource(thatParent);
        }
    }

    /**
     * remove messageSource
     *
     * @param messageSource to be removed
     */
    public void removeMessage(HierarchicalMessageSource messageSource) {
        addMessage(messageSource, Integer.MIN_VALUE);
    }


    private <T extends Map<Locale, ?>> void remove(Map<String, T> map, String code, Locale locale) {
        Map<Locale, ?> mapStr = map.get(code);
        if (mapStr != null) {
            mapStr.remove(locale);
            if (mapStr.isEmpty()) {
                map.remove(code);
            }
        }
    }

    private record OrderedMessageSource(HierarchicalMessageSource source, int order) {
    }
}

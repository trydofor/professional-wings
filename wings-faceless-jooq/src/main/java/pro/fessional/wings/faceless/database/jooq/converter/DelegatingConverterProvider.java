package pro.fessional.wings.faceless.database.jooq.converter;

import org.jetbrains.annotations.Nullable;
import org.jooq.Converter;
import org.jooq.ConverterProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-01-14
 */
public class DelegatingConverterProvider implements ConverterProvider {

    private final List<ConverterProvider> providers = new ArrayList<>();
    private final List<Converter<?, ?>> converters = new ArrayList<>();

    @Override
    public @Nullable <T, U> Converter<T, U> provide(Class<T> tType, Class<U> uType) {
        for (ConverterProvider provider : providers) {
            final Converter<T, U> converter = provider.provide(tType, uType);
            if (converter != null) return converter;
        }
        return null;
    }

    public synchronized void add(ConverterProvider provider) {
        if (provider instanceof DelegatingConverterProvider) {
            final List<ConverterProvider> pds = ((DelegatingConverterProvider) provider).providers;
            providers.addAll(pds);
        } else {
            providers.add(provider);
        }
    }

    public synchronized void add(Collection<ConverterProvider> providers) {
        for (ConverterProvider provider : providers) {
            add(provider);
        }
    }

    public synchronized <T, U> void add(final Converter<T, U> converter) {
        if (converters.isEmpty()) {
            providers.add(new ConverterProvider() {
                @Override
                @SuppressWarnings("unchecked")
                public @Nullable <A, B> Converter<A, B> provide(Class<A> tType, Class<B> uType) {
                    for (Converter<?, ?> conv : converters) {
                        if (tType.equals(conv.fromType()) && uType.equals(conv.toType())) {
                            return (Converter<A, B>) conv;
                        }
                    }
                    return null;
                }
            });
        }
        converters.add(converter);
    }
}

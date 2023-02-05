package pro.fessional.wings.silencer.spring.help;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * init on ApplicationPreparedEvent
 *
 * @author trydofor
 * @since 2022-08-12
 */
public class ApplicationContextHelper {

    private static ConfigurableApplicationContext context;
    private static ConfigurableEnvironment environment;

    protected ApplicationContextHelper(ConfigurableApplicationContext ctx) {
        context = Objects.requireNonNull(ctx);
        environment = Objects.requireNonNull(ctx.getEnvironment());
    }

    /**
     * 获取spring.application.name属性 或 context#getApplicationName
     */
    @NotNull
    public static String getApplicationName() {
        String app = environment.getProperty("spring.application.name");
        if (app == null || app.isEmpty()) {
            app = context.getApplicationName();
        }
        return app;
    }

    @NotNull
    public static ConfigurableApplicationContext getContext() {
        return context;
    }

    @NotNull
    public static ConfigurableEnvironment getEnvironment() {
        return environment;
    }

    public static String getProperties(String name) {
        return environment.getProperty(name);
    }

    public static <T> T getProperties(String name, Class<T> type) {
        return environment.getProperty(name, type);
    }

    /**
     * 获取所有key和有效值，按key的出现顺序显示
     */
    @NotNull
    public static Map<String, String> listProperties() {
        final LinkedHashMap<String, String> prop = new LinkedHashMap<>();

        final Map<String, List<String>> map = listPropertySource();
        final LinkedHashSet<String> keys = new LinkedHashSet<>();
        for (List<String> st : map.values()) {
            keys.addAll(st);
        }

        for (String key : keys) {
            if (!key.equals(PropertySourceUnsupported)) {
                prop.put(key, environment.getProperty(key));
            }
        }

        return prop;
    }


    /**
     * 显示keys及对应的层叠后的来源
     */
    @NotNull
    public static Map<String, String> listPropertiesKeys() {
        final LinkedHashMap<String, String> prop = new LinkedHashMap<>();

        for (Map.Entry<String, List<String>> en : listPropertySource().entrySet()) {
            final List<String> keys = en.getValue();
            for (String key : keys) {
                prop.putIfAbsent(key, en.getKey());
            }
        }

        return prop;
    }

    /**
     * 获取PropertySources及其内keys，keys可重复，因文件可层叠
     */
    @NotNull
    public static Map<String, List<String>> listPropertySource() {
        final LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();
        for (PropertySource<?> ps : environment.getPropertySources()) {
            walkPropertySource("", map, ps);
        }
        final List<String> old = map.remove(PropertySourceUnsupported);
        if (old != null) {
            map.put(PropertySourceUnsupported, old);
        }
        return map;
    }

    public static final String PropertySourceUnsupported = "__PropertySourceUnsupported__";
    public static final String PropertySourceDelimiter = ":";

    /**
     * 用于调查source和key的关系，返回 source:source2#key - key的map
     */
    public static void walkPropertySource(String root, Map<String, List<String>> srcKey, PropertySource<?> src) {
        // EnvironmentEndpoint
        if (ConfigurationPropertySources.isAttachedConfigurationPropertySource(src)) return;
        String prefix = root == null || root.isEmpty() ? "" : root + PropertySourceDelimiter;
        if (src instanceof CompositePropertySource cps) {
            for (PropertySource<?> ps : cps.getPropertySources()) {
                walkPropertySource(prefix + ps.getName(), srcKey, ps);
            }
        }
        else if (src instanceof EnumerablePropertySource<?> eps) {
            for (String key : eps.getPropertyNames()) {
                srcKey.computeIfAbsent(prefix + src.getName(), ignoredK -> new ArrayList<>())
                      .add(key);

            }
        }
        else {
            srcKey.computeIfAbsent(PropertySourceUnsupported, ignoredK -> new ArrayList<>())
                  .add(prefix + src.getName() + PropertySourceDelimiter + src.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> T getBean(String name) {
        return (T) context.getBean(name);
    }

    @NotNull
    public static <T> T getBean(Class<T> type) {
        return context.getBean(type);
    }

    @NotNull
    public static <T> ObjectProvider<T> getBeanProvider(Class<T> type) {
        return context.getBeanProvider(type);
    }

    public static String getMessage(String code, Locale locale, Object... arg) {
        return context.getMessage(code, arg, locale);
    }

    public static Resource getResource(String local) {
        return context.getResource(local);
    }

    @SneakyThrows
    @NotNull
    public static Resource[] getResources(String local) {
        return context.getResources(local);
    }
}

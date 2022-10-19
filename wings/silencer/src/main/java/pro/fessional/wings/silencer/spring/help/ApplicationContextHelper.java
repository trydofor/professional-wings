package pro.fessional.wings.silencer.spring.help;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;

import java.util.Locale;
import java.util.Objects;

/**
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

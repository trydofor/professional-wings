package pro.fessional.wings.faceless.database.helper;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.text.CaseSwitcher;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2022-03-21
 */
public class DatabaseNaming {

    private static final ConcurrentHashMap<Class<?>, String> classTableCache = new ConcurrentHashMap<>();

    public static String tableName(@NotNull Class<?> clazz) {
        return classTableCache.computeIfAbsent(clazz, k -> {
            String name = clazz.getSimpleName();
            return name.endsWith("Table") ? lowerSnake(name.substring(0, name.length() - 5)) : lowerSnake(name);
        });
    }

    public static String lowerSnake(String name) {
        return CaseSwitcher.snake(name);
    }
}

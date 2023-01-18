package pro.fessional.wings.tiny.task.schedule.help;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * @author trydofor
 * @since 2022-12-12
 */
public class TaskerHelper {

    public static final char MethodPrefix = '#';

    public static String tokenize(@NotNull Class<?> beanClass, String method) {
        StringBuilder sb = new StringBuilder(128);
        sb.append(beanClass.getName());
        if (method != null && !method.isEmpty()) {
            sb.append(MethodPrefix).append(method);
        }
        return sb.toString();
    }

    @SneakyThrows
    public static Method referMethod(@NotNull String token) {
        int p = token.indexOf(MethodPrefix);
        if (p <= 0) {
            throw new IllegalArgumentException("miss method part, token=" + token);
        }
        final Class<?> clz = ClassUtils.forName(token.substring(0, p), null);
        final String mdn = token.substring(p + 1);
        for (Method md : clz.getDeclaredMethods()) {
            if (md.getName().equals(mdn)) {
                return md;
            }
        }
        throw new NoSuchMethodException(token);
    }

    @SneakyThrows
    public static Class<?> referClass(@NotNull String token) {
        int p = token.indexOf(MethodPrefix);
        String clz = p > 0 ? token.substring(0, p) : token;
        return ClassUtils.forName(clz, null);
    }

    @SneakyThrows
    public static boolean acceptToken(@NotNull Class<?> beanClass, Method method, String token) {
        if (token == null || token.isEmpty()) return false;
        final int mp = token.lastIndexOf(MethodPrefix);
        String md = null;
        String cn = token;
        if (mp > 0) {
            md = token.substring(mp + 1);
            cn = token.substring(0, mp);
        }
        //
        final Class<?> superClass = Class.forName(cn);
        if (!superClass.isAssignableFrom(beanClass)) {
            return false;
        }

        if (md != null) {
            return method.getName().equals(md);
        }

        return true;
    }

    @SneakyThrows
    public static boolean acceptBean(@NotNull Class<?> superClass, @NotNull Object beanThis, Object beanThat) {
        if (beanThat == null) return false;
        if (beanThis == beanThat) return true;
        return superClass.isAssignableFrom(AopUtils.getTargetClass(beanThat));
    }
}

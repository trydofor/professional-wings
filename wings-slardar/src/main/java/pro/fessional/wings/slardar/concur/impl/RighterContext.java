package pro.fessional.wings.slardar.concur.impl;

import org.jetbrains.annotations.Contract;
import pro.fessional.wings.slardar.concur.RighterException;

import java.util.function.Consumer;

/**
 * 在Controller层通过interceptor，防止数据权限提升。
 * 原则是谁杀谁埋，埋点的数据会以header形式response
 * 请求编辑时，设置allow，提交修改时，检查audit
 *
 * @author trydofor
 * @since 2021-03-27
 */
public class RighterContext {

    private static final ThreadLocal<Consumer<Object>> ResAllow = new ThreadLocal<>();
    private static final ThreadLocal<Object> ReqAudit = new ThreadLocal<>();

    /**
     * 设置允许项，在response前由controller使用
     */
    public static void setAllow(Object obj) {
        final Consumer<Object> fun = ResAllow.get();
        if (fun == null) {
            throw new IllegalStateException("must use in @Righter method");
        }
        fun.accept(obj);
    }

    /**
     * 获得允许项，在拦截器内部使用
     */
    public static void funAllow(Consumer<Object> fun) {
        ResAllow.set(fun);
    }

    /**
     * 移除允许项，在拦截器内部使用
     */
    public static void delAllow() {
        ResAllow.remove();
    }

    /**
     * 获得审查项，在controller中，业务前使用
     */
    @SuppressWarnings("unchecked")
    @Contract("true ->!null")
    public static <R> R getAudit(boolean nonnull) {
        final Object obj = ReqAudit.get();
        if (obj == null && nonnull) {
            throw new RighterException("failed to audit null");
        }
        return (R) obj;
    }

    /**
     * 设置审查项，在拦截器中内部使用
     */
    public static void setAudit(Object json) {
        ReqAudit.set(json);
    }

    /**
     * 移除审查项，在拦截器中内部使用
     */
    public static void delAudit() {
        ReqAudit.remove();
    }
}

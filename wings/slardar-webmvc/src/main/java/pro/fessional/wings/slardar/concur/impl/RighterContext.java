package pro.fessional.wings.slardar.concur.impl;

import org.jetbrains.annotations.Contract;
import pro.fessional.wings.slardar.concur.RighterException;

import java.util.function.Consumer;

/**
 * <pre>
 * In the Controller layer through the interceptor to prevent data privilege elevation.
 * The principle is who create and who destroy, audit data will respond in the form of header.
 * When requesting edits, set allow, and when submitting changes, check and audit.
 * </pre>
 *
 * @author trydofor
 * @since 2021-03-27
 */
public class RighterContext {

    /** no leak, for static */
    private static final ThreadLocal<Consumer<Object>> ResAllow = new ThreadLocal<>();
    /** no leak, for static */
    private static final ThreadLocal<Object> ReqAudit = new ThreadLocal<>();

    /**
     * Set the `allow` items. Used in the controller, before response
     */
    public static void setAllow(Object obj) {
        final Consumer<Object> fun = ResAllow.get();
        if (fun == null) {
            throw new IllegalStateException("must use in @Righter method");
        }
        fun.accept(obj);
    }

    /**
     * Set the `allow` items. Used in the interceptor.
     */
    public static void funAllow(Consumer<Object> fun) {
        ResAllow.set(fun);
    }

    /**
     * Delete the `allow` items. Used in the interceptor.
     */
    public static void delAllow() {
        ResAllow.remove();
    }

    /**
     * Get the `audit` items. Used in the controller, before business
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
     * Get the `audit` items. Used in the interceptor.
     */
    public static void setAudit(Object json) {
        ReqAudit.set(json);
    }

    /**
     * Delete the `audit` items. Used in the interceptor.
     */
    public static void delAudit() {
        ReqAudit.remove();
    }
}

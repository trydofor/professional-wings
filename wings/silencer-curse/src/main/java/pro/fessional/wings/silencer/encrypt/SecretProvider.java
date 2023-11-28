package pro.fessional.wings.silencer.encrypt;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.code.RandCode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * System password provider, default 256bit, 32 characters
 *
 * @author trydofor
 * @since 2022-12-05
 */
public class SecretProvider {

    /**
     * Default password length
     */
    public static final int Length = 32;

    /**
     * System default, randomly generated each time the system starts, disappears after downtime.
     */
    public static final String System = "system";

    /**
     * Used for Api Tickets, should be the same within the cluster
     */
    public static final String Ticket = "ticket";

    /**
     * Used for Http Cookie, should be the same within the cluster
     */
    public static final String Cookie = "cookie";
    /**
     * Used for sensitive data in profiles, it is recommended to fix value
     */
    public static final String Config = "config";

    protected SecretProvider(@NotNull Map<String, String> keys) {
        Secrets.putAll(keys);
    }

    /**
     * Generate `len` length passwords of alphabetic, case-sensitive and numeric
     *
     * @see RandCode#strong(int)
     */
    @NotNull
    public static String strong(int len) {
        return RandCode.strong(len);
    }

    /**
     * Generate `len` length passwords with good readability of case-sensitive letters and numbers.
     * total 32 Letter and Numbers, removing 30 (0oO,1il,cC,j,kK,mM,nN,pP,sS,uU,vV,wW,xX,y,zZ)
     *
     * @see RandCode#human(int)
     */
    @NotNull
    public static String human(int len) {
        return RandCode.human(len);
    }

    /**
     * Get the secret by name, if not found, generate a default length strong password and cache it.
     */
    @NotNull
    public static String get(String name) {
        return get(name, true);
    }

    /**
     * Get the secret by name, if not found and computeIfAbsent, generate a default length strong password and cache it.
     */
    @Contract("_,true->!null")
    public static String get(String name, boolean computeIfAbsent) {
        if (name == null || name.isEmpty()) {
            name = System;
        }

        if (computeIfAbsent) {
            return Secrets.computeIfAbsent(name, k -> RandCode.strong(Length));
        }
        else {
            return Secrets.get(name);
        }
    }

    /**
     * used for `@Bean` method
     */
    @Nullable
    public String tryGet(String name) {
        return get(name, false);
    }

    //
    protected static final ConcurrentHashMap<String, String> Secrets = new ConcurrentHashMap<>();

    /**
     * put the secret by name
     */
    public static void put(@NotNull String name, @NotNull String secret, boolean replace) {
        if (replace) {
            Secrets.put(name, secret);
        }
        else {
            Secrets.putIfAbsent(name, secret);
        }
    }
}

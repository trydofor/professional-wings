package pro.fessional.wings.silencer.encrypt;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.bits.Aes256;

/**
 * @author trydofor
 * @since 2022-12-05
 */
public class Aes256Provider {

    @NotNull
    public static Aes256 system() {
        return get(SecretProvider.System, true);
    }

    @NotNull
    public static Aes256 ticket() {
        return get(SecretProvider.Ticket, true);
    }

    @NotNull
    public static Aes256 cookie() {
        return get(SecretProvider.Cookie, true);
    }

    @NotNull
    public static Aes256 config() {
        return get(SecretProvider.Config, true);
    }

    @Contract("_,true->!null")
    public static Aes256 get(String name, boolean nonnull) {
        final String key = SecretProvider.get(name, false);
        if (key == null) {
            if (nonnull) {
                throw new IllegalStateException("must init before using");
            }
            else {
                return null;
            }
        }

        return Aes256.of(key);
    }
}

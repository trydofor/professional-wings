package pro.fessional.wings.slardar.security;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.data.Null;

import java.util.Map;

/**
 * @author trydofor
 * @since 2021-02-08
 */
public interface WingsAuthTypeParser {

    /**
     * Convert the string alias to enum, return Null.Enm instead of null
     *
     * @param authType authType
     * @return strong type of authType
     * @see Null#Enm
     */
    @NotNull
    Enum<?> parse(String authType);

    /**
     * Convert the enum to string alias, throws if fail
     *
     * @param authType authType
     * @return string alias
     * @throws IllegalArgumentException if fail
     */
    @NotNull
    String parse(Enum<?> authType);

    /**
     * Get all the string alias and enum one-to-one mapping
     */
    @NotNull
    Map<String, Enum<?>> types();
}

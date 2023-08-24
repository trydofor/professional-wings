package pro.fessional.wings.slardar.security;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author trydofor
 * @since 2022-01-18
 */
public interface WingsAuthDetails {

    /**
     * Get meta-info to help with validation, such as the parameters within a request.
     *
     * @return mutable, not thread-safe Map
     */
    @NotNull
    Map<String, String> getMetaData();

    /**
     * Get the detail data after build
     *
     * @return detail data, eg. Oauth
     */
    @Nullable
    Object getRealData();
}

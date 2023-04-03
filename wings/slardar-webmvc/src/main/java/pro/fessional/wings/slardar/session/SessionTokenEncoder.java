package pro.fessional.wings.slardar.session;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * encode/deconde between session id and session token
 *
 * @author trydofor
 * @since 2023-03-30
 */
public interface SessionTokenEncoder {

    /**
     * encode session id to token
     */
    @NotNull
    String encode(@NotNull String id, @NotNull HttpServletRequest request);

    /**
     * decode session token to id
     */
    @NotNull
    List<String> decode(@NotNull List<String> tokens, @NotNull HttpServletRequest request);
}

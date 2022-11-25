package pro.fessional.wings.slardar.httprest.okhttp;

import okhttp3.Call;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;

/**
 * @author trydofor
 * @since 2022-11-25
 */
public interface OkHttpBuildableClient {
    @NotNull
    Call newCall(@NotNull Request.Builder builder);
}

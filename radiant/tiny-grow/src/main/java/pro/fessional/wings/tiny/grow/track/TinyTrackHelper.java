package pro.fessional.wings.tiny.grow.track;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.function.SingletonSupplier;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;

import java.util.function.Consumer;

/**
 * @author trydofor
 * @since 2024-07-25
 */
public class TinyTrackHelper {

    public static SingletonSupplier<TinyTrackService> TrackService = ApplicationContextHelper.getSingletonSupplier(TinyTrackService.class);

    /**
     * never throw
     */
    public static void track(Consumer<TinyTrackService.Tracking> fulfill) {

    }

    /**
     * never throw
     */
    public static void track(@NotNull String key, @NotNull String ref, @NotNull Consumer<TinyTrackService.Tracking> fulfill) {

    }
}

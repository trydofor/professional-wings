package pro.fessional.wings.example.service.journal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.faceless.service.journal.JournalService;

/**
 * @author trydofor
 * @since 2019-08-15
 */
public class JournalServiceSpringSecurity implements JournalService {

    @NotNull
    @Override
    public Journal commit(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo) {
        return null;
    }

    @NotNull
    @Override
    public Journal commit(@NotNull Class<?> eventClass, @Nullable String loginInfo, @Nullable Object targetKey, @Nullable Object otherInfo) {
        return null;
    }

    @NotNull
    @Override
    public Journal commit(@NotNull Class<?> eventClass, @Nullable Object targetKey, @Nullable Object otherInfo) {
        return null;
    }

    @NotNull
    @Override
    public Journal commit(@NotNull Class<?> eventClass, @Nullable Object targetKey) {
        return null;
    }

    @NotNull
    @Override
    public Journal commit(@NotNull Class<?> eventClass) {
        return null;
    }
}

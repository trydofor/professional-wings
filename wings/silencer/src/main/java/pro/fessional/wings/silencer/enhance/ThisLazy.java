package pro.fessional.wings.silencer.enhance;

import org.jetbrains.annotations.NotNull;

/**
 * @author trydofor
 * @since 2024-05-10
 */
@SuppressWarnings("unchecked")
public class ThisLazy<T> implements ThisLazyAware<T> {

    @NotNull
    protected T thisLazy = (T) this;

    @Override
    public void setThisLazy(@NotNull T self) {
        this.thisLazy = self;
    }
}

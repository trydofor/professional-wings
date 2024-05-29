package pro.fessional.wings.silencer.enhance;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;

/**
 * <pre>
 * &#64;Setter(onMethod_ = {&#64;Autowired, &#64;Lazy})
 * protected RuntimeConfServiceImpl thisLazy = this;
 *
 * Except for the following cases, there are runtime type exceptions,
 * where `M` represents the enhanced method used by thisLazy,
 * * `T` is an interface, and all `M` come from `T` (best practice)
 * * `T` is a class, and `M` is enhanced by Cglib (proxyTargetClass=true)
 * * no `M`, in which case `T` is itself (but should not use this pattern)
 * </pre>
 *
 * @author trydofor
 * @since 2024-05-10
 */
public interface ThisLazyAware<T> {

    /**
     * inject enhanced this before Bean Initialization
     *
     * @param thisLazy enhanced bean
     */
    void setThisLazy(@NotNull T thisLazy);

    /**
     * the type of thisLazy, Object to skip check.
     */
    @NotNull
    default Class<?> thisLazyType() {
        return (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}

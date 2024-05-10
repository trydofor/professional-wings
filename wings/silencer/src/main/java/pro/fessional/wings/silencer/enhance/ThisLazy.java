package pro.fessional.wings.silencer.enhance;

/**
 * <pre>
 * &#64;Setter(onMethod_ = {&#64;Autowired, &#64;Lazy})
 * protected RuntimeConfServiceImpl thisLazy = this;
 * </pre>
 *
 * @author trydofor
 * @since 2024-05-10
 */
@SuppressWarnings("unchecked")
public class ThisLazy<T> implements ThisLazyAware<T> {

    protected T thisLazy = (T) this;

    @Override
    public void setThisLazy(T self) {
        this.thisLazy = self;
    }
}

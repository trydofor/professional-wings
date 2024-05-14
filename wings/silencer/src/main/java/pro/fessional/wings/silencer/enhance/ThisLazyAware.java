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
public interface ThisLazyAware<T> {

    /**
     * inject enhanced this before Bean Initialization
     *
     * @param thisLazy enhanced bean
     */
    void setThisLazy(T thisLazy);
}

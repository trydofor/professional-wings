package pro.fessional.wings.slardar.cache;

/**
 * @author trydofor
 * @since 2022-04-18
 */
public class WingsEvict {

    public static final WingsEvict ALL = new WingsEvict(null);

    private final boolean all;
    private final Object key;

    public WingsEvict(Object key) {
        this.all = key == null;
        this.key = key;
    }

    public static WingsEvict all() {
        return ALL;
    }

    public static WingsEvict key(Object key) {
        return new WingsEvict(key);
    }
}

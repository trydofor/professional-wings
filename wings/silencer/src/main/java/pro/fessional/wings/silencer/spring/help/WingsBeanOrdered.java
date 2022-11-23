package pro.fessional.wings.silencer.spring.help;

import org.springframework.core.Ordered;

/**
 * @author trydofor
 * @since 2022-11-03
 */
public interface WingsBeanOrdered extends Ordered {
    int BaseLine = -10_000_000;
    int WatchingAround = BaseLine - 100;
}

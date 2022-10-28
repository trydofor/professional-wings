package pro.fessional.wings.silencer.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.DynamicThresholdFilter;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

/**
 * 根据MDC中的LoggerName和WingLevel调整阈值。
 * <p>
 * https://logback.qos.ch/manual/filters.html
 *
 * @author trydofor
 * @see DynamicThresholdFilter
 * @since 2022-10-28
 */
public class WingsThresholdFilter extends TurboFilter {


    @Override public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        return null;
    }
}

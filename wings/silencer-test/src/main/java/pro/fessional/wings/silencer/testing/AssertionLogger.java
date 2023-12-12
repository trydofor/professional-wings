package pro.fessional.wings.silencer.testing;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;

import org.jetbrains.annotations.Contract;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;


/**
 * @author trydofor
 * @since 2023-11-01
 */
public class AssertionLogger extends AppenderBase<ILoggingEvent> {

    /**
     * install, and add rules, start, then assert, finally uninstall.
     */
    public static AssertionLogger install() {
        final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        AssertionLogger memoryAppender = new AssertionLogger();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        root.addAppender(memoryAppender);
        return memoryAppender;
    }

    private final Map<String, Predicate<ILoggingEvent>> rules = new LinkedHashMap<>();
    private final Map<String, Integer> count = new LinkedHashMap<>();

    @Contract("_,_->this")
    public AssertionLogger rule(String name, Predicate<ILoggingEvent> rule) {
        rules.put(name, rule);
        count.put(name, 0);
        return this;
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted()) return;

        for (Map.Entry<String, Predicate<ILoggingEvent>> en : rules.entrySet()) {
            if (en.getValue().test(event)) {
                count.compute(en.getKey(), (s, c) -> c == null ? 1 : c + 1);
            }
        }
    }

    public Map<String, Integer> getAssertCount() {
        return count;
    }

    public int getAssertCount(String name) {
        Integer c = count.get(name);
        return c == null ? 0 : c;
    }

    public boolean assertAllTrue() {
        for (Integer c : count.values()) {
            if (c == null || c <= 0) return false;
        }
        return true;
    }


    public void resetAll() {
        rules.clear();
        count.clear();
    }

    public void resetCount() {
        count.clear();
    }

    public void uninstall() {
        resetAll();
        final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        for (Iterator<Appender<ILoggingEvent>> iter = root.iteratorForAppenders(); iter.hasNext(); ) {
            Appender<ILoggingEvent> apd = iter.next();
            if (apd == this) root.detachAppender(apd);
        }
    }
}

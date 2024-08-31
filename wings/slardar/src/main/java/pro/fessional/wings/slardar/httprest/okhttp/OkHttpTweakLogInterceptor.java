package pro.fessional.wings.slardar.httprest.okhttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.logging.LogLevel;
import pro.fessional.mirana.time.StopWatch;
import pro.fessional.wings.silencer.tweak.TweakLogger;
import pro.fessional.wings.silencer.watch.Watches;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.boot.logging.LogLevel.DEBUG;
import static org.springframework.boot.logging.LogLevel.ERROR;
import static org.springframework.boot.logging.LogLevel.FATAL;
import static org.springframework.boot.logging.LogLevel.INFO;
import static org.springframework.boot.logging.LogLevel.OFF;
import static org.springframework.boot.logging.LogLevel.TRACE;
import static org.springframework.boot.logging.LogLevel.WARN;

/**
 * <pre>
 * Default mapping of Log-level and data-level
 * DEBUG- : BODY
 * INFO : BASIC
 * WARN+ : NONE
 * </pre>
 *
 * @author trydofor
 * @since 2022-11-01
 */
@Slf4j
public class OkHttpTweakLogInterceptor implements OkHttpInterceptor {

    public static final HttpLoggingInterceptor.Logger LoggerTrace = log::trace;
    public static final HttpLoggingInterceptor.Logger LoggerDebug = log::debug;
    public static final HttpLoggingInterceptor.Logger LoggerInfo = log::info;
    public static final HttpLoggingInterceptor.Logger LoggerWarn = log::warn;

    private final EnumMap<LogLevel, HttpLoggingInterceptor> mapping = new EnumMap<>(LogLevel.class);
    private final Set<String> nopUrlToken = new HashSet<>();

    public OkHttpTweakLogInterceptor(Collection<String> nop) {
        resetMapping();
        TweakLogger.asCoreLevel(log.getName());
        nopUrlToken.addAll(nop);
    }

    /**
     * Change the mapping to Log-level and data-level, e.g. DEBUG - BODY
     */
    public void levelMapping(@NotNull LogLevel lg, @NotNull Level ok) {
        final HttpLoggingInterceptor.Logger okl;
        if (lg == TRACE || lg == DEBUG) {
            okl = LoggerDebug;
        }
        else if (lg == INFO) {
            okl = LoggerInfo;
        }
        else {
            okl = LoggerWarn;
        }

        final HttpLoggingInterceptor itc = new HttpLoggingInterceptor(okl);
        itc.setLevel(ok);
        mapping.put(lg, itc);
    }

    /**
     * reset to the default mapping
     */
    public void resetMapping() {
        HttpLoggingInterceptor none = new HttpLoggingInterceptor(LoggerWarn);
        none.setLevel(Level.NONE);
        HttpLoggingInterceptor basic = new HttpLoggingInterceptor(LoggerInfo);
        basic.setLevel(Level.BASIC);
        HttpLoggingInterceptor body = new HttpLoggingInterceptor(LoggerDebug);
        body.setLevel(Level.BODY);
        //
        mapping.put(OFF, none);
        mapping.put(FATAL, none);
        mapping.put(ERROR, none);
        mapping.put(WARN, none);
        mapping.put(LogLevel.INFO, basic);
        mapping.put(LogLevel.DEBUG, body);
        mapping.put(LogLevel.TRACE, body);
    }


    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        boolean off = false;
        if (!nopUrlToken.isEmpty()) {
            String url = chain.request().url().toString();
            for (String tkn : nopUrlToken) {
                if (StringUtils.containsIgnoreCase(url, tkn)) {
                    log.debug("exclude intercept, token={}, url={}", tkn, url);
                    off = true;
                    break;
                }
            }
        }

        final LogLevel lvl = off ? LogLevel.OFF : TweakLogger.currentLevel(log.getName());
        final HttpLoggingInterceptor itc = mapping.get(lvl);

        final StopWatch current = Watches.current();
        if (current == null) {
            return itc.intercept(chain);
        }

        // watch if the caller thread has watching
        final Request request = chain.request();
        final String name = "OkHttp " + request.method() + " " + request.url();
        try (StopWatch.Watch ignore = current.start(name)) {
            return itc.intercept(chain);
        }
        finally {
            Watches.release(true);
        }
    }

    @Override
    public boolean isNetwork() {
        return true;
    }
}

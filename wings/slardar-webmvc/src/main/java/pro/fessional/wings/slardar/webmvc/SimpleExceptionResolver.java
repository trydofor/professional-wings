package pro.fessional.wings.slardar.webmvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@RequiredArgsConstructor
@Slf4j
public class SimpleExceptionResolver<T extends Exception> extends WingsExceptionResolver<T> {

    protected final SimpleResponse defaultResponse;

    @Override
    protected SimpleResponse resolve(@NotNull T ce) {
        return defaultResponse;
    }
}

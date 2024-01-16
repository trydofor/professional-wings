package org.slf4j;

import org.jetbrains.annotations.NotNull;
import org.slf4j.spi.MDCAdapter;

/**
 * @author trydofor
 * @since 2024-01-16
 */
public class TweakMDC {
    public static void adapt(@NotNull MDCAdapter mdc) {
        // init static block
        var ignore = MDC.getMDCAdapter();
        // replace current adapter
        MDC.mdcAdapter = mdc;
    }
}

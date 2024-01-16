package pro.fessional.wings.silencer.tweak;

import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.slf4j.spi.MDCAdapter;
import pro.fessional.mirana.time.Sleep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author trydofor
 * @since 2024-01-16
 */
@Slf4j
//@SpringBootTest
class TtlMDCAdapterTest {

    @Test
    @TmsLink("C11030")
    public void testTtlMDC() {
//        TweakMDC.adapt(new TtlMDCAdapter());

        String key = "key";
        String value = "value";
        MDC.put(key, value);
        final StringBuilder sb = new StringBuilder();
        new Thread(() -> {
            String v = MDC.get(key);
            if (v != null) {
                sb.append(v);
            }
            MDC.remove(key);
            assertNull(MDC.get(key));
        }).start();

        Sleep.ignoreInterrupt(1000);

        MDCAdapter adapter = MDC.getMDCAdapter();
        log.info("MDCAdapter={}", adapter.getClass());
        if (adapter instanceof TtlMDCAdapter) {
            assertEquals(value, sb.toString());
            assertNull(MDC.get(key));
        }
        else {
            assertEquals("", sb.toString());
            assertEquals(value, MDC.get(key));
        }
    }
}
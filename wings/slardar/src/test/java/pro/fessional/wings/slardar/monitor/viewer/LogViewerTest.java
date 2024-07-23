package pro.fessional.wings.slardar.monitor.viewer;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.spring.prop.SlardarMonitorProp;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author trydofor
 * @since 2024-07-23
 */
@SpringBootTest
class LogViewerTest {

    @Setter(onMethod_ = { @Autowired })
    protected SlardarMonitorProp slardarMonitorProp;

    @TmsLink("13128")
    @Test
    void canIgnoreHead() throws Exception {
        final LogViewer lv = new LogViewer(slardarMonitorProp.getView());

        final Path tmp0 = Files.createTempFile("test-", null);
        tmp0.toFile().deleteOnExit();
        Files.writeString(tmp0, """
            2024-07-21 22:05:22.957 ERROR 10884 --- [kite-front] [XNIO-1 I/O-4] io.undertow.request                      : UT005071: Undertow request failed HttpServerExchange{ CONNECT api.ipify.org:443}
                        
            java.lang.IllegalArgumentException: UT000068: Servlet path match failed
            	at io.undertow.servlet.handlers.ServletPathMatchesData.getServletHandlerByPath(ServletPathMatchesData.java:83) ~[undertow-servlet-2.3.10.Final.jar!/:2.3.10.Final]
            """.stripIndent());


        boolean b0 = lv.canIgnoreHead(tmp0.toAbsolutePath().toString());
        Assertions.assertTrue(b0);

        final Path tmp1 = Files.createTempFile("test-", null);
        tmp1.toFile().deleteOnExit();
        Files.writeString(tmp1, """
            2024-07-21 22:05:22.957 ERROR 10884 --- [kite-front] [XNIO-1 I/O-4] io.undertow.request                      : UT005071: Undertow request failed HttpServerExchange{ CONNECT api.ipify.org:443}
                        
            java.lang.IllegalArgumentException: UT000068: Servlet path match failed
            	at io.undertow.servlet.handlers.ServletPathMatchesData.getServletHandlerByPath(ServletPathMatchesData.java:83) ~[undertow-servlet-2.3.10.Final.jar!/:2.3.10.Final]
            	at io.undertow.servlet.handlers.ServletPathMatches.getServletHandlerByPath(ServletPathMatches.java:133) ~[undertow-servlet-2.3.10.Final.jar!/:2.3.10.Final]

            2024-07-23 01:33:05.446 ERROR 10884 --- [kite-front] [XNIO-1 task-4] p.f.w.w.e.DefaultExceptionResolver       : unhandled exception, response default
              
            java.lang.NumberFormatException: For input string
                      """.stripIndent());


        boolean b1 = lv.canIgnoreHead(tmp1.toAbsolutePath().toString());
        Assertions.assertFalse(b1);
    }
}
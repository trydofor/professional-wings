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
        final LogViewer lv = new LogViewer(slardarMonitorProp.getView(), slardarMonitorProp.genRuleKey());

        final Path tmp0 = Files.createTempFile("test-", null);
        tmp0.toFile().deleteOnExit();
        Files.writeString(tmp0, """
            ######### #1 KEYWORD:  ERROR  #########
            2024-08-28 07:01:54.441 ERROR 3306238 --- [admin-test] [XNIO-1 I/O-2] io.undertow.request                      : UT005071: Undertow request failed HttpServerExchange{ CONNECT eth0.me:443}
            
            java.lang.IllegalArgumentException: UT000068: Servlet path match failed
                at io.undertow.servlet.handlers.ServletPathMatchesData.getServletHandlerByPath(ServletPathMatchesData.java:83) ~[undertow-servlet-2.3.13.Final.jar!/:2.3.13.Final]
                at io.undertow.servlet.handlers.ServletPathMatches.getServletHandlerByPath(ServletPathMatches.java:133) ~[undertow-servlet-2.3.13.Final.jar!/:2.3.13.Final]
                at io.undertow.servlet.handlers.ServletInitialHandler.handleRequest(ServletInitialHandler.java:148) ~[undertow-servlet-2.3.13.Final.jar!/:2.3.13.Final]
                at io.undertow.server.handlers.HttpContinueReadHandler.handleRequest(HttpContinueReadHandler.java:69) ~[undertow-core-2.3.13.Final.jar!/:2.3.13.Final]
                at org.springframework.boot.web.embedded.undertow.DeploymentManagerHttpHandlerFactory$DeploymentManagerHandler.handleRequest(DeploymentManagerHttpHandlerFactory.java:74) ~[spring-boot-3.2.8.jar!/:3.2.8]
                at io.undertow.server.handlers.GracefulShutdownHandler.handleRequest(GracefulShutdownHandler.java:94) ~[undertow-core-2.3.13.Final.jar!/:2.3.13.Final]
                at io.undertow.server.Connectors.executeRootHandler(Connectors.java:393) ~[undertow-core-2.3.13.Final.jar!/:2.3.13.Final]
                at io.undertow.server.protocol.http.HttpReadListener.handleEventWithNoRunningRequest(HttpReadListener.java:265) ~[undertow-core-2.3.13.Final.jar!/:2.3.13.Final]
                at io.undertow.server.protocol.http.HttpReadListener.handleEvent(HttpReadListener.java:136) ~[undertow-core-2.3.13.Final.jar!/:2.3.13.Final]
                at io.undertow.server.protocol.http.HttpReadListener.handleEvent(HttpReadListener.java:59) ~[undertow-core-2.3.13.Final.jar!/:2.3.13.Final]
                at org.xnio.ChannelListeners.invokeChannelListener(ChannelListeners.java:92) ~[xnio-api-3.8.8.Final.jar!/:3.8.8.Final]
                at org.xnio.conduits.ReadReadyHandler$ChannelListenerHandler.readReady(ReadReadyHandler.java:66) ~[xnio-api-3.8.8.Final.jar!/:3.8.8.Final]
                at org.xnio.nio.NioSocketConduit.handleReady(NioSocketConduit.java:89) ~[xnio-nio-3.8.8.Final.jar!/:3.8.8.Final]
                at org.xnio.nio.WorkerThread.run(WorkerThread.java:591) ~[xnio-nio-3.8.8.Final.jar!/:3.8.8.Final]
            
            2024-08-28 07:02:13.000 DEBUG 3306238 --- [admin-test] [task-7] org.jooq.tools.LoggerListener            : Executing query          : update `win_task_define` set `next_lock` = (`next_lock` + ?), `last_exec` = ? where (`id` = ? and `next_lock` = ?)
            2024-08-28 07:02:13.000 DEBUG 3306238 --- [admin-test] [task-7] org.jooq.tools.LoggerListener            : -> with bind values      : update `win_task_define` set `next_lock` = (`next_lock` + 1), `last_exec` = {ts '2024-08-28 07:02:13.0'} where (`id` = 1107 and `next_lock` = 89552)
            2024-08-28 07:02:13.005 DEBUG 3306238 --- [admin-test] [task-7] org.jooq.tools.LoggerListener            : Affected row(s)          : 1
            2024-08-28 07:02:13.005  INFO 3306238 --- [admin-test] [task-7] p.f.w.t.t.s.i.TinyTaskExecServiceImpl    : tiny-task exec, id=1107, prop=bill-expire
            2024-08-28 07:02:13.006  INFO 3306238 --- [admin-test] [task-7] p.f.w.t.t.s.i.TinyTaskExecServiceImpl    : tiny-task done, id=1107, prop=bill-expire
            2024-08-28 07:02:13.007 DEBUG 3306238 --- [admin-test] [light-id-buffered-provider-2] o.s.jdbc.core.JdbcTemplate               : Executing prepared SQL query
            2024-08-28 07:02:13.007 DEBUG 3306238 --- [admin-test] [light-id-buffered-provider-2] o.s.jdbc.core.JdbcTemplate               : Executing prepared SQL statement [SELECT next_val, step_val FROM sys_light_sequence WHERE block_id=? AND seq_name=? FOR UPDATE]
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
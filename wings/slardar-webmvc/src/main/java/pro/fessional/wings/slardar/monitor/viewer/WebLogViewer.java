package pro.fessional.wings.slardar.monitor.viewer;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.spring.prop.SlardarMonitorProp;

import java.io.IOException;

/**
 * @author trydofor
 * @since 2021-07-20
 */
@Slf4j
@RestController
@ConditionalWingsEnabled(abs = LogConf.Key$enable)
public class WebLogViewer extends LogViewer {

    @Autowired
    public WebLogViewer(SlardarMonitorProp prop) {
        super(prop.getView());
    }

    @Operation(summary = "Alarm logs can be viewed in conjunction with alarm notifications when self-monitoring is enabled.", description = """
        # Usage
        Pass the log id to view the log.
        ## Params
        * @param id - log id, max 2k caches in 36H
        ## Returns
        * @return {200 | string} log context or empty""")
    @GetMapping(value = "${" + LogConf.Key$mapping + "}")
    public void view(@RequestParam("id") String id, HttpServletResponse res) throws IOException {
        super.view(id, res.getOutputStream());
    }
}

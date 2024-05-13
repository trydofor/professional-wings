package pro.fessional.wings.slardar.webmvc;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import de.codecentric.boot.admin.server.web.servlet.AdminControllerHandlerMapping;
import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

/**
 * @author trydofor
 * @since 2024-05-13
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.boot.admin.server.enabled=true",
        })
@EnableAdminServer
public class BootAdminServerTest {

    @Setter(onMethod_ = {@Autowired})
    protected List<RequestMappingHandlerMapping> requestMappingHandlerMapping;

    /**
     * <pre>
     * default order,
     * 1.ControllerEndpointHandlerMapping for @ControllerEndpoint and @RestControllerEndpoint
     * 2.RequestMappingHandlerMapping for @RequestMapping
     * 3.AdminControllerHandlerMapping for BootAdminServer
     *
     * after reorder
     * 1.ControllerEndpointHandlerMapping for @ControllerEndpoint and @RestControllerEndpoint
     * 2.AdminControllerHandlerMapping for BootAdminServer
     * 3.RequestMappingHandlerMapping for @RequestMapping
     * </pre>
     */
    @Test
    @TmsLink("C13121")
    public void mappingOrder() {
        int good = 0;
        for (RequestMappingHandlerMapping mapping : requestMappingHandlerMapping) {
            if (mapping instanceof AdminControllerHandlerMapping) {
                good = 1;
            }
            else {
                if (good > 0) good++;
            }
        }
        Assertions.assertTrue(good >= 2);
    }
}

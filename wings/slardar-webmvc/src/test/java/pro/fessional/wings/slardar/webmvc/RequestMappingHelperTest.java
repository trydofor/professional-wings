package pro.fessional.wings.slardar.webmvc;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootTest
@Slf4j
public class RequestMappingHelperTest {

    @Setter(onMethod_ = {@Autowired})
    private ApplicationContext context;

    @Test
    @TmsLink("C13112")
    public void infoAllMapping() {
        List<RequestMappingHelper.Info> infos = RequestMappingHelper.infoAllMapping(context);
        for (RequestMappingHelper.Info info : infos) {
            log.info(info.toJson());
        }
    }
}

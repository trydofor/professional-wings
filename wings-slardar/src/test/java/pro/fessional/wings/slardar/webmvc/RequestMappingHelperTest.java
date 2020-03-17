package pro.fessional.wings.slardar.webmvc;

import lombok.Setter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RequestMappingHelperTest {

    @Setter(onMethod = @__({@Autowired}))
    private ApplicationContext context;

    @Test
    public void infoAllMapping() {
        List<RequestMappingHelper.Info> infos = RequestMappingHelper.infoAllMapping(context);
        for (RequestMappingHelper.Info info : infos) {
            System.out.println(info.toJson());
        }
    }
}
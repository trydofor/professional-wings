package pro.fessional.wings.batrider.controller;

import org.apache.servicecomb.provider.pojo.Invoker;
import org.apache.servicecomb.provider.pojo.RpcReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author trydofor
 * @since 2022-08-04
 */
@RestController
public class BatriderController {

    public interface Hello {
        String sayHello(String name);
    }

    private final Hello batriderHello = Invoker.createProxy("batrider", "BatriderServcomber", Hello.class);

    @RpcReference(microserviceName = "winx-api", schemaId = "ApiServcomber")
    private Hello winxApiHello;

    @RequestMapping(path = "/batrider/winxApiHello", method = RequestMethod.GET)
    public String winxApiHello(@RequestParam(name = "name") String name) {

        return winxApiHello.sayHello(name);
    }

    @RequestMapping(path = "/batrider/batriderHello", method = RequestMethod.GET)
    public String batriderHello(@RequestParam(name = "name") String name) {
        return batriderHello.sayHello(name);
    }
}

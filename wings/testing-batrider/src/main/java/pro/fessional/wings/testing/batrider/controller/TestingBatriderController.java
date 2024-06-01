package pro.fessional.wings.testing.batrider.controller;

import lombok.Setter;
import org.apache.servicecomb.provider.pojo.Invoker;
import org.apache.servicecomb.provider.pojo.RpcReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pro.fessional.wings.testing.batrider.contract.TestingHelloContract;

/**
 * @author trydofor
 * @since 2022-08-04
 */
@RestController
public class TestingBatriderController {

    private final TestingHelloContract testingHelloContractProxy = Invoker.createProxy("batrider", "batrider-hello", TestingHelloContract.class);

    @Setter(onMethod_ = { @RpcReference(microserviceName = "winx-api", schemaId = "winx-hello") })
    private TestingHelloContract testingHelloContractRpc;

    @Setter(onMethod_ = { @Autowired })
    private RestTemplate restTemplate;

    @RequestMapping(path = "/batrider/winx-hello-rpc", method = RequestMethod.GET)
    public String winxHelloRpc(@RequestParam(name = "name") String name) {
        return testingHelloContractRpc.sayHello(name);
    }

    @RequestMapping(path = "/batrider/winx-hello-cse", method = RequestMethod.GET)
    public String winxHelloCse(@RequestParam(name = "name") String name) {
        return restTemplate.getForObject("cse://winx-api/winx-hello/say-hello?name=" + name, String.class);
//        return restTemplate.getForObject("cse://winx-api/winx-hello/say-hello?name={name}", String.class, name);
    }

    @RequestMapping(path = "/batrider/batx-hello-pxy", method = RequestMethod.GET)
    public String batriderHelloPxy(@RequestParam(name = "name") String name) {
        return testingHelloContractProxy.sayHello(name);
    }
}

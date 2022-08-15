package com.moilioncircle.wings.api.contractor;

import com.moilioncircle.wings.api.contract.HelloContract;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author trydofor
 * @since 2022-08-04
 */
@RestSchema(schemaId = "winx-hello")
@RequestMapping(path = "/")
public class HelloContractor implements HelloContract {

    @RequestMapping(path = "/winx-hello/say-hello", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @Override
    public String sayHello(@RequestParam(name = "name") String name) {
        return "Winx: Hello " + name;
    }
}

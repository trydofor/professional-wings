package com.moilioncircle.wings.api.servcomber;

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
@RestSchema(schemaId = "ApiServcomber")
public class ApiServcomber {

    @RequestMapping(path = "/hello", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String sayHello(@RequestParam(name = "name") String name) {
        return "Hello " + name;
    }
}

package com.moilioncircle.wings.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author trydofor
 * @since 2022-08-04
 */
@RestController
public class HelloController {

    @RequestMapping(path = "/api/say-hello", method = RequestMethod.GET)
    public String sayHello(@RequestParam(name = "name") String name) {
        return "hello " + name;
    }

}

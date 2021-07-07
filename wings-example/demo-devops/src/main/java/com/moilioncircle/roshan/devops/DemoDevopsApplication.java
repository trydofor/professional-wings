package com.moilioncircle.roshan.devops;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@SpringBootApplication
@EnableAdminServer
public class DemoDevopsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoDevopsApplication.class, args);
    }
}

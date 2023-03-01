package pro.fessional.wings.slardar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author trydofor
 * @since 2021-02-01
 */
@RestController
@Slf4j
public class TestForwardController {

    @GetMapping("/test/forward.json")
    public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/test/forward-target.json")
               .forward(request, response);
        log.info(">>>>>>>>after forward<<<<<<<");
    }

    @GetMapping("/test/forward-target.json")
    public String forward() {
        return "forward-target";
    }
}

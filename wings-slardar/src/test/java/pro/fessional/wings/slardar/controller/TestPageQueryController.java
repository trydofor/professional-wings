package pro.fessional.wings.slardar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;

import java.util.Arrays;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
public class TestPageQueryController {

    @RequestMapping({"/test/page-request.html"})
    @ResponseBody
    public PageResult<String> pageQuery(@ModelAttribute PageQuery page) {
        List<String> data = Arrays.asList("1", "2");
        return PageResult.of(100, data, page);
    }
}

package pro.fessional.wings.slardar.app.controller;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.mirana.page.PageDefault;
import pro.fessional.mirana.page.PageQuery;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
public class TestPageQueryController {

    @RequestMapping({"/test/page-request.html"})
    @ResponseBody
    public PageQuery pageQuery(@ModelAttribute PageQuery page) {
        return page;
    }

    @RequestMapping({"/test/page-request-0.html"})
    @ResponseBody
    public PageQuery pageQuery0(PageQuery page) {
        return page;
    }

    @RequestMapping({"/test/page-request-1.html"})
    @ResponseBody
    public PageQuery pageQuery1(@PageDefault PageQuery page) {
        return page;
    }

    @RequestMapping({"/test/page-request-2.html"})
    @ResponseBody
    public PageQuery pageQuery2(@PageDefault(size = 22, page = 2, sortAlias = "sb") PageQuery page) {
        return page;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Ins extends PageQuery{
        private String name;
    }

    @RequestMapping({"/test/page-request-3.html"})
    @ResponseBody
    // no alias enchantment, Jackson conversion only.
    public Ins pageQuery3(@RequestBody Ins ins) {
        return ins;
    }

    @RequestMapping({"/test/page-request-4.html"})
    @ResponseBody
    // MessageConvertor can NOT handle @PageDefault
    public Ins pageQuery4(@RequestBody @PageDefault(size = 22, page = 2) Ins ins) {
        return ins;
    }
}

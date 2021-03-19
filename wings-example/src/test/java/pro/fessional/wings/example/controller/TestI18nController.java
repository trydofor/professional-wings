package pro.fessional.wings.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.context.TerminalContext;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-06-30
 */

@Controller
@Slf4j
@RequiredArgsConstructor
public class TestI18nController {

    private final MessageSource messageSource;

    @RequestMapping({"/", "/index.html"})
    public String index(HttpServletRequest request, Model model) {
        ZonedDateTime now = ZonedDateTime.now();
        ZoneId systemZoneId = ZoneId.systemDefault();
        Locale systemLocale = Locale.getDefault();

        TerminalContext.Context ctx = SecurityContextUtil.getTerminalContext();
        Locale userLocale = ctx.getLocale();
        ZoneId userZoneId = ctx.getTimeZone().toZoneId();
        String userDatetime = DateFormatter.full19(now, userZoneId);
        //
        model.addAttribute("userLocale", userLocale);
        model.addAttribute("userZoneId", userZoneId);
        model.addAttribute("userDatetime", userDatetime);
        //
        model.addAttribute("messageUserHello", messageSource.getMessage("user.hello", new Object[]{}, userLocale));

        model.addAttribute("systemLocale", systemLocale);
        model.addAttribute("systemZoneId", systemZoneId);
        String systemDatetime = DateFormatter.full19(now);
        model.addAttribute("systemDatetime", systemDatetime);

        return "index";
    }
}

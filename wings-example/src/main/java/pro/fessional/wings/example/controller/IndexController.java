package pro.fessional.wings.example.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.wings.silencer.context.WingsI18nContext;
import pro.fessional.wings.silencer.spring.help.WingsI18nWebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-06-30
 */

@Controller
@AllArgsConstructor
@Slf4j
public class IndexController {

    private final MessageSource messageSource;

    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {
        ZonedDateTime now = ZonedDateTime.now();
        ZoneId systemZoneId = ZoneId.systemDefault();
        WingsI18nContext ctx = WingsI18nWebUtil.getI18nContext(request);
        if (ctx != null) {
            @NotNull Locale locale = ctx.getLocale();
            @NotNull ZoneId zoneId = ctx.getZoneId();
            String userDatetime = DateFormatter.full19(now, zoneId);
            //
            model.addAttribute("userLocale", locale);
            model.addAttribute("userZoneId", zoneId);
            model.addAttribute("userDatetime", userDatetime);
            //
            log.info("user.hello=" + messageSource.getMessage("user.hello", new Object[]{}, locale));
            log.info("userLocale=" + locale);
            log.info("userZoneId=" + zoneId);
            log.info("userDatetime=" + userDatetime);
        }

        model.addAttribute("systemZoneId", systemZoneId);
        String systemDatetime = DateFormatter.full19(now);
        model.addAttribute("systemDatetime", systemDatetime);

        log.info("systemZoneId=" + systemZoneId);
        log.info("systemDatetime=" + systemDatetime);

        return "index";
    }
}

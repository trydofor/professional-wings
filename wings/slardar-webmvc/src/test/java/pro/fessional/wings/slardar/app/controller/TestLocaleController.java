package pro.fessional.wings.slardar.app.controller;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.errcode.AuthnErrorEnum;
import pro.fessional.wings.slardar.servlet.resolver.WingsLocaleResolver;

import java.util.Locale;

/**
 * @author trydofor
 * @see WingsLocaleResolver
 * @since 2021-02-01
 */
@RestController
public class TestLocaleController {

    @Setter(onMethod_ = {@Autowired})
    private MessageSource messageSource;

    @GetMapping("/test/i18n-message.json")
    public String login() {
        final Locale lang = TerminalContext.currentLocale();
        final String msg = messageSource.getMessage(AuthnErrorEnum.BadCredentials.getCode(), null, lang);
        return msg + "|" + lang.toLanguageTag();
    }
}

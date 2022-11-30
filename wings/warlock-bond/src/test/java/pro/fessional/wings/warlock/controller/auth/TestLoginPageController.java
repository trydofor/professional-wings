package pro.fessional.wings.warlock.controller.auth;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.warlock.event.auth.WarlockNonceSendEvent;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2021-02-27
 */
@RestController
@Slf4j
public class TestLoginPageController {

    @Setter(onMethod_ = {@Autowired})
    private WingsAuthTypeParser authTypeParser;

    @GetMapping("/auth/console-nonce.json")
    public String loginPageDefault(@RequestParam("username") String user, @RequestParam(value = "authtype", required = false) String type) {
        Enum<?> authType = authTypeParser.parse(type);
        String pass = RandCode.human(16);
        long expire = System.currentTimeMillis() + 300_000;
        WarlockNonceSendEvent event = new WarlockNonceSendEvent();
        event.setAuthType(authType);
        event.setExpired(expire);
        event.setUsername(user);
        event.setNonce(pass);
        EventPublishHelper.AsyncSpring.publishEvent(event);
        log.warn("{} {} nonce is {}", user, type, pass);
        return pass;
    }

    @GetMapping("/auth/list-auth.json")
    public Set<String> listAllAuth() {
        final Collection<GrantedAuthority> auth = SecurityContextUtil.getAuthorities();
        return auth.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }
}

package pro.fessional.wings.warlock.app.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.faceless.enums.autogen.StandardTimezone;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.warlock.event.auth.WarlockNonceSendEvent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2021-02-27
 */
@RestController
@Slf4j
public class TestLoginController {

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

    @GetMapping("/auth/list-hold.json")
    public Set<String> listAllHold() {
        final TerminalContext.Context cxt = TerminalContext.get();
        return cxt.getAuthPerm();
    }

    /**
     * Need to set @Parameter(hidden = true)
     */
    @GetMapping("/auth/current-principal.json")
    public R<WingsUserDetails> currentPrincipal(@Parameter(hidden = true) @AuthenticationPrincipal WingsUserDetails principal) {
        final Authentication authn = SecurityContextHolder.getContext().getAuthentication();
        log.info("current Authentication={}", authn);
        return R.okData(principal);
    }

    @Data
    public static class Dto {
        private final String zid;
        private final ZonedDateTime zdt;
    }

    @GetMapping("/auth/list-zoneid.json")
    public Dto listZoneId() {
        final ZoneId zid = LocaleContextHolder.getTimeZone().toZoneId();
        LocalDate ld = LocalDate.of(2021, 1, 1);
        LocalTime lt = LocalTime.of(5, 0, 0, 0);
        ZonedDateTime zdt = ZonedDateTime.of(ld, lt, StandardTimezone.GMT.toZoneId());
        // America/New_York
        return new Dto(zid.getId(), zdt);
    }

    @GetMapping("/admin/need-auth.json")
    public String adminAuth() {
        return "admin need auth";
    }
}

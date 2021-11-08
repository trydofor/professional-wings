package pro.fessional.wings.warlock.controller.user;

import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 需要配置权限，通过filter配置或方法级的@PreAuthorize("isAuthenticated()")
 *
 * @author trydofor
 * @since 2021-02-16
 */
// @PreAuthorize("isAuthenticated()")
@RestController
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = WarlockEnabledProp.Key$controllerUser, havingValue = "true")
public class AuthedUserController {

    @Data
    public static class Dto {
        private String nickname;
        private String locale;
        private String zoneid;
        private int offset;
        private String authtype;
        private String token;
    }

    @ApiOperation(value = "获得登录用户的自身基本信息，未登录时Result(false)；可设置URL权限，返回status=401")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$userAuthedUser + "}")
    public R<Dto> authedUser(HttpServletRequest request) {
        final WingsUserDetails wd = SecurityContextUtil.getDetails();
        if (wd == null) return R.ng();

        Dto dto = new Dto();
        final Enum<?> at = wd.getAuthType();
        if (at != null) {
            dto.setAuthtype(at.name());
        }
        dto.setNickname(wd.getNickname());
        dto.setLocale(wd.getLocale().toLanguageTag());
        final ZoneId zid = wd.getZoneId();
        dto.setZoneid(zid.getId());
        dto.setOffset(ZonedDateTime.now(zid).getOffset().getTotalSeconds());
        final HttpSession session = request.getSession(false);
        if (session != null) {
            dto.setToken(session.getId());
        }
        return R.okData(dto);
    }

    @Data
    public static class Ins {
        private Map<String, String> alias;
        private Set<String> perms;
    }

    @ApiOperation(value = "检查登录用户的权限，不区分大小写比较，返回存在的权限；未登录时Result(false)，可设置URL权限，返回status=401",
            notes = "alias优先于perms检测，alias表示以其value返回，以方便历史命名映射。")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$userAuthedPerm + "}")
    public R<Set<String>> authedPerm(@RequestBody Ins ins) {
        final WingsUserDetails wd = SecurityContextUtil.getDetails();
        if (wd == null) return R.ng();

        Set<String> perms = ins.getPerms();
        if (perms == null) perms = Collections.emptySet();

        Map<String, String> alias = ins.getAlias();
        if (alias == null) alias = Collections.emptyMap();

        if (perms.isEmpty() && alias.isEmpty()) {
            return R.ok();
        }

        // alias 优先于perms
        Map<String, String> ci = new HashMap<>();
        for (String p : perms) {
            ci.put(p.toLowerCase(), p);
        }
        for (Map.Entry<String, String> e : alias.entrySet()) {
            ci.put(e.getKey().toLowerCase(), e.getValue());
        }

        Set<String> res = new HashSet<>();
        for (GrantedAuthority an : wd.getAuthorities()) {
            final String p = an.getAuthority().toLowerCase();
            final String a = ci.get(p);
            if (a != null) {
                res.add(a);
            }
        }

        return R.okData(res);
    }
}

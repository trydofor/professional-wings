package pro.fessional.wings.warlock.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.MapSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.slardar.session.SessionTokenEncoder;
import pro.fessional.wings.slardar.session.WingsSessionHelper;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Permissions need to be configured by filter config or method-level `@PreAuthorize("isAuthenticated()")`
 *
 * @author trydofor
 * @since 2021-02-16
 */
// @PreAuthorize("isAuthenticated()")
@RestController
@ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$mvcUser)
@RequiredArgsConstructor
@Slf4j
public class AuthedUserController {

    @Setter(onMethod_ = {@Autowired})
    private WingsSessionHelper wingsSessionHelper;
    @Setter(onMethod_ = {@Autowired(required = false)})
    private SessionTokenEncoder sessionTokenEncoder;

    @Schema(description = "Basic info of login user")
    @Data
    public static class Dto {
        @Schema(description = "nickname", example = "trydofor")
        private String nickname;
        @Schema(description = "username", example = "trydofor")
        private String username;
        @Schema(description = "language, see java.util.Locale", example = "zh-CN")
        private String locale;
        @Schema(description = "timezone, see java.time.ZoneId", example = "Asia/Shanghai")
        private String zoneid;
        @Schema(description = "time offset in second to UTD", example = "28800")
        private int offset;
        @Schema(description = "auth type of current session", example = "EMAIL")
        private String authtype;
        @Schema(description = "auth token of current session", example = "fd7a5475-bd3b-4086-96b0-b95d11cf1d3c")
        private String token;
    }

    @Operation(summary = "Get authed info of current user", description = """
            # Usage
            Only logined user
            ## Returns
            * @return {200 | Result(Dto)} logined user and basis info
            * @return {200 | Result(false)} not logined and the URL without perm
            * @return {401} logined and no perm to the URL""")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$userAuthedUser + "}")
    public R<Dto> authedUser(HttpServletRequest request) {
        final WingsUserDetails wd = SecurityContextUtil.getUserDetails(false);
        if (wd == null) return R.NG();

        Dto dto = new Dto();
        fillDetail(wd, dto);
        final HttpSession session = request.getSession(false);
        if (session != null) {
            String sid = session.getId();
            if (sessionTokenEncoder != null) {
                sid = sessionTokenEncoder.encode(sid, request);
            }
            dto.setToken(sid);
        }
        return R.okData(dto);
    }

    private void fillDetail(WingsUserDetails wd, Dto dto) {
        final Enum<?> at = wd.getAuthType();
        if (at != null) {
            dto.setAuthtype(at.name());
        }
        dto.setNickname(wd.getNickname());
        dto.setUsername(wd.getUsername());
        dto.setLocale(wd.getLocale().toLanguageTag());
        final ZoneId zid = wd.getZoneId();
        dto.setZoneid(zid.getId());
        dto.setOffset(ZonedDateTime.now(zid).getOffset().getTotalSeconds());
    }


    @Data
    @Schema(description = "Check the perm/role of login user")
    public static class Ins {
        @Schema(description = "original as key, alias as value", example = "{\"ROLE_SYSTEM\":\"OLD_SYSTEM\"}")
        private Map<String, String> alias;
        @Schema(description = "set of perm/role", example = "[\"ROLE_ADMIN\",\"ROLE_SYSTEM\"]")
        private Set<String> perms;
        @Schema(description = "perm/role to check, if not contain (all/any), then invalidate session", example = "[\"ROLE_ADMIN\"]")
        private Set<String> check;
        @Schema(description = "check any or all", example = "true")
        private boolean any = false;
    }

    @Operation(summary = "Check the perm/role (case-insensitive) of the current user and returns the existing", description = """
            # Usage
            alias takes precedence over perm, and auto logout if check fails.
            ## Params
            * @param ins.alias - alias as map value for historical legacy
            * @param ins.perms - perm/role original name
            * @param ins.check - perm/role to check
            * @param ins.any - check any or all
            ## Returns
            * @return {200 | Result(string[])} logined and perms
            * @return {200 | Result(false)} not logined and the URL without perm
            * @return {200 | Result(false,string[])} check fail, return failed perm and invalidate session
            * @return {401} logined and no perm to the URL""")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$userAuthedPerm + "}")
    public R<Set<String>> authedPerm(HttpServletRequest request, @RequestBody Ins ins) {
        final WingsUserDetails wd = SecurityContextUtil.getUserDetails(false);
        if (wd == null) return R.NG();

        final Set<String> ck = ins.getCheck();
        final Set<String> pm = wd.getAuthorities().stream()
                                 .map(it -> it.getAuthority().toLowerCase())
                                 .collect(Collectors.toSet());

        if (ck != null && !ck.isEmpty()) {
            final Set<String> ng = new HashSet<>();
            for (String s : ck) {
                if (!pm.contains(s.toLowerCase())) {
                    ng.add(s);
                }
            }

            int ns = ng.size();
            if ((ns > 0 && !ins.any) || (ins.any && ns == ck.size())) {
                request.getSession().invalidate();
                return R.ngData(ng);
            }
        }

        Set<String> perms = ins.getPerms();
        if (perms == null) perms = Collections.emptySet();

        Map<String, String> alias = ins.getAlias();
        if (alias == null) alias = Collections.emptyMap();

        if (perms.isEmpty() && alias.isEmpty()) {
            return R.OK();
        }

        // alias over perms
        Map<String, String> ci = new HashMap<>();
        for (String p : perms) {
            ci.put(p.toLowerCase(), p);
        }
        for (Map.Entry<String, String> e : alias.entrySet()) {
            ci.put(e.getKey().toLowerCase(), e.getValue());
        }

        Set<String> res = new HashSet<>();
        for (String p : pm) {
            final String a = ci.get(p);
            if (a != null) {
                res.add(a);
            }
        }

        return R.okData(res);
    }

    @Schema(description = "Session info of logined user")
    @Data @EqualsAndHashCode(callSuper = true)
    public static class Ses extends Dto {
        @Schema(description = "Whether expired", example = "true")
        private boolean expired;
        @Schema(description = "Latest access time", example = "true")
        private ZonedDateTime lastAccess;
    }

    @Operation(summary = "List all session of current user", description = """
            # Usage
            Only the logined user
            ## Returns
            * @return {200 | Result(Dto)} logined and basis info
            * @return {200 | Result(false)} not logined and the URL without perm
            * @return {401} logined and no perm to the URL""")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$userListSession + "}")
    public R<List<Ses>> listSession() {
        final WingsUserDetails details = SecurityContextUtil.getUserDetails(false);
        if (details == null) return R.NG();

        final List<MapSession> sessions = wingsSessionHelper.findByUserId(details.getUserId());
        final List<Ses> sess = sessions.stream().map(it -> {
            Ses ses = new Ses();
            ses.setToken(it.getId());
            ses.setExpired(it.isExpired());
            ses.setLastAccess(it.getLastAccessedTime().atZone(details.getZoneId()));

            final SecurityContext ctx = wingsSessionHelper.getSecurityContext(it);
            final WingsUserDetails dtl = SecurityContextUtil.getUserDetails(ctx);
            if (dtl != null) {
                fillDetail(dtl, ses);
            }

            return ses;
        }).collect(Collectors.toList());

        return R.okData(sess);
    }

    @Data
    public static class Sid {
        private String sid;
    }

    @Operation(summary = "drop the session of current user by id", description = """
            # Usage
            Only the logined user
            ## Params
            * @param sid - sessionId/token to drop
            ## Returns
            * @return {200 | Result(Dto)} logined
            * @return {200 | Result(false)} not logined and the URL without perm
            * @return {401} logined and no perm to the URL""")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$userDropSession + "}")
    public R<Boolean> dropSession(@RequestBody Sid sid) {
        final boolean b = wingsSessionHelper.dropSession(sid.sid);
        return R.okData(b);
    }
}

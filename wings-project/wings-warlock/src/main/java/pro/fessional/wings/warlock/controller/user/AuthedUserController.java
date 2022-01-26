package pro.fessional.wings.warlock.controller.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import java.util.stream.Collectors;

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

    @ApiModel(description = "登录用户基本信息")
    @Data
    public static class Dto {
        @ApiModelProperty(value = "昵称", example = "trydofor")
        private String nickname;
        @ApiModelProperty(value = "语言，参考java.util.Locale", example = "zh-CN")
        private String locale;
        @ApiModelProperty(value = "时区，参考java.time.ZoneId", example = "Asia/Shanghai")
        private String zoneid;
        @ApiModelProperty(value = "秒差，与UTC相差的秒数", example = "28800")
        private int offset;
        @ApiModelProperty(value = "验证类型，此session的登录类型", example = "EMAIL")
        private String authtype;
        @ApiModelProperty(value = "验证凭证，此session的登录凭证", example = "fd7a5475-bd3b-4086-96b0-b95d11cf1d3c")
        private String token;
    }

    @ApiOperation(value = "获得登录用户的基本信息", notes =
            "# Usage \n"
            + "只有登录用户才有信息\n"
            + "## Params \n"
            + "无\n"
            + "## Returns \n"
            + "* @return {200 | Result(Dto)} 登录用户，成功返回用户基本信息；\n"
            + "* @return {200 | Result(false)} 未登录用户，且无URL权限；\n"
            + "* @return {401} 若设置了URL访问权限且用户未登录；")
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
    @ApiModel(description = "精查登录用户角色权限")
    public static class Ins {
        @ApiModelProperty(value = "别名项，本名为key，别名为value", example = "{\"ROLE_SYSTEM\":\"OLD_SYSTEM\"}")
        private Map<String, String> alias;
        @ApiModelProperty(value = "权限项", example = "[\"ROLE_ADMIN\",\"ROLE_SYSTEM\"]")
        private Set<String> perms;
        @ApiModelProperty(value = "检查项，若检查项未全满足，则invalidate session", example = "[\"ROLE_ADMIN\"]")
        private Set<String> check;
    }

    @ApiOperation(value = "检查登录用户的权限，不区分大小写比较，返回存在的权限；", notes =
            "# Usage \n"
            + "alias优先于perms检测，check失败时会自动登出logout。\n"
            + "## Params \n"
            + "* @param ins.alias - 以本名为key，别名为value，返回别名，以兼容历史遗留\n"
            + "* @param ins.perms - 权限或角色的本名\n"
            + "* @param ins.check - 需要检查的权限或角色的本名\n"
            + "## Returns \n"
            + "* @return {200 | Result(string[])} 登录用户，成功返回用户基本信息；\n"
            + "* @return {200 | Result(false)} 未登录用户，且无URL权限；\n"
            + "* @return {200 | Result(false,string[])} check失败，返回失败的权限且invalidate session；\n"
            + "* @return {401} 若设置了URL访问权限且用户未登录；")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$userAuthedPerm + "}")
    public R<Set<String>> authedPerm(HttpServletRequest request, @RequestBody Ins ins) {
        final WingsUserDetails wd = SecurityContextUtil.getDetails();
        if (wd == null) return R.ng();

        final Set<String> ck = ins.getCheck();
        final Set<String> pm = wd.getAuthorities().stream()
                                 .map(it -> it.getAuthority().toLowerCase())
                                 .collect(Collectors.toSet());

        if (ck != null && !ck.isEmpty()) {
            final Set<String> lt = new HashSet<>();
            for (String s : ck) {
                if (!pm.contains(s.toLowerCase())) {
                    lt.add(s);
                }
            }
            if (!lt.isEmpty()) {
                request.getSession().invalidate();
                return R.ngData(lt);
            }
        }

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
        for (String p : pm) {
            final String a = ci.get(p);
            if (a != null) {
                res.add(a);
            }
        }

        return R.okData(res);
    }
}

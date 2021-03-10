package pro.fessional.wings.example.service.authrole;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.math.AnyIntegerUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Service
@Setter(onMethod_ = {@Autowired})
public class AuthRoleService {

    private AuthRoleCache authRoleCache;

    public Set<GrantedAuthority> loadRoleAuth(String roleSet, String authSet) {
        Map<Integer, String> auths = new HashMap<>();
        fillByRole(auths, StringUtils.split(roleSet, ","));
        String[] aid = StringUtils.split(authSet, ",");
        AuthEnumUtil.fillAuth(auths, aid);
        AuthEnumUtil.trimAuth(auths);
        return auths.values()
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
    }


    public void fillByRole(Map<Integer, String> map, String... ids) {
        if (ids == null || ids.length == 0) return;
        for (String id : ids) {
            Map<Integer, String> authCode = authRoleCache.loadAuth(AnyIntegerUtil.val64(id));
            map.putAll(authCode);
        }
    }
}

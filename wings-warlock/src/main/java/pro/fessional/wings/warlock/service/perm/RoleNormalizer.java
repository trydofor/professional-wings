package pro.fessional.wings.warlock.service.perm;

import lombok.Getter;
import lombok.Setter;

/**
 * 加工role，增加prefix
 *
 * @author trydofor
 * @since 2021-06-08
 */
public class RoleNormalizer {

    @Setter @Getter
    private String prefix = "ROLE_";

    public String normalize(String name) {
        return name.startsWith(prefix) ? name : prefix + name;
    }

    public boolean hasPrefix(String name) {
        return name.startsWith(prefix);
    }
}

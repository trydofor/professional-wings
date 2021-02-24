package pro.fessional.wings.warlock.security.handler;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthPageHandler;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-02-19
 */
public class LoginPageDefaultHandler implements ComboWingsAuthPageHandler.Combo {

    public static final int ORDER = WarlockOrderConst.AuthPageCombo + 90;

    @Setter(onMethod = @__({@Autowired}))
    private WarlockSecurityProp warlockSecurityProp;
    @Setter
    @Getter
    private int order = ORDER;

    @Override
    public ResponseEntity<?> response(@Nullable Enum<?> authType, @Nullable MimeType mimeType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        return ResponseEntity.ok().body(R.okData(warlockSecurityProp.getAuthType().keySet()));
    }
}

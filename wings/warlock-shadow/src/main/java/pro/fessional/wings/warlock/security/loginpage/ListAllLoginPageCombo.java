package pro.fessional.wings.warlock.security.loginpage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthPageHandler;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

/**
 * @author trydofor
 * @since 2021-02-19
 */
public class ListAllLoginPageCombo implements ComboWingsAuthPageHandler.Combo {

    @Setter @Getter
    private int order = OrderedWarlockConst.SecListAllLoginPageCombo;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockSecurityProp warlockSecurityProp;

    @Override
    public ResponseEntity<?> response(@NotNull Enum<?> authType, @Nullable MediaType mediaType, @NotNull HttpServletRequest request,
                                      @NotNull HttpServletResponse response, @NotNull HttpStatus status) {
        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(R.okData(warlockSecurityProp.getAuthType().keySet()));
    }
}

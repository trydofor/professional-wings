package pro.fessional.wings.warlock.security.loginpage;

import lombok.Getter;
import lombok.Setter;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthPageHandler;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.security.justauth.JustAuthRequestBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-02-19
 */
public class JustAuthLoginPageCombo implements ComboWingsAuthPageHandler.Combo {

    public static final int ORDER = WarlockOrderConst.AuthPageCombo + 9_000;

    @Setter @Getter
    private int order = ORDER;

    @Setter(onMethod_ = {@Autowired})
    protected JustAuthRequestBuilder justAuthRequestBuilder;

    @Override
    public ResponseEntity<?> response(@NotNull Enum<?> authType, @Nullable MediaType mediaType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        final AuthRequest ar = justAuthRequestBuilder.buildRequest(authType);
        if (ar == null) return null;

        final String authorize = ar.authorize(AuthStateUtils.createState());
        if (mediaType != null && mediaType.getSubtype().contains("html")) {
            // response.sendRedirect(url); 302
            return ResponseEntity.status(HttpStatus.FOUND)
                                 .contentType(MediaType.TEXT_XML)
                                 .header(HttpHeaders.LOCATION, authorize)
                                 .build();
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .contentType(mediaType == null ? MediaType.APPLICATION_JSON : mediaType)
                                 .body(R.okData(authorize));
        }
    }
}

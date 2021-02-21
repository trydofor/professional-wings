package pro.fessional.wings.warlock.security.justauth;

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
import org.springframework.util.MimeType;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthPageHandler;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-02-19
 */
public class JustAuthLoginPageCombo implements ComboWingsAuthPageHandler.Combo {

    @Setter(onMethod = @__({@Autowired}))
    private JustAuthRequestBuilder justAuthRequestBuilder;
    @Setter
    @Getter
    private int order = WarlockOrderConst.AuthPageCombo + 10;

    @Override
    public ResponseEntity<?> response(@Nullable Enum<?> authType, @Nullable MimeType mimeType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        final AuthRequest ar = justAuthRequestBuilder.buildRequest(authType);
        if (ar == null) return null;

        final String authorize = ar.authorize(AuthStateUtils.createState());
        if (MediaType.TEXT_HTML == mimeType) {
            // response.sendRedirect(url); 302
            return ResponseEntity.status(HttpStatus.FOUND)
                                 .header(HttpHeaders.LOCATION, authorize)
                                 .build();
        } else {
            return ResponseEntity.ok().body(R.okData(authorize));
        }
    }
}

package pro.fessional.wings.warlock.security.loginpage;

import lombok.Getter;
import lombok.Setter;
import me.zhyd.oauth.request.AuthRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthPageHandler;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.security.justauth.AuthStateBuilder;
import pro.fessional.wings.warlock.security.justauth.JustAuthRequestBuilder;
import pro.fessional.wings.warlock.security.session.NonceTokenSessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-02-19
 */
public class JustAuthLoginPageCombo implements ComboWingsAuthPageHandler.Combo {

    @Setter @Getter
    private int order = WarlockOrderConst.JustAuthLoginPageCombo ;

    @Setter(onMethod_ = {@Autowired})
    protected JustAuthRequestBuilder justAuthRequestBuilder;

    @Setter(onMethod_ = {@Autowired})
    protected WingsRemoteResolver wingsRemoteResolver;

    @Setter(onMethod_ = {@Autowired})
    protected AuthStateBuilder authStateBuilder;

    @Override
    public ResponseEntity<?> response(@NotNull Enum<?> authType, @Nullable MediaType mediaType, @NotNull HttpServletRequest request,
                                      @NotNull HttpServletResponse response, @NotNull HttpStatus status) {
        final AuthRequest ar = justAuthRequestBuilder.buildRequest(authType, request);
        if (ar == null) return null;

        final String state = authStateBuilder.buildState(request);
        NonceTokenSessionHelper.initNonce(state, wingsRemoteResolver.resolveRemoteKey(request));

        final String authorize = ar.authorize(state);
        if (mediaType != null && mediaType.getSubtype().contains("html")) {
            // response.sendRedirect(url); 302
            return ResponseEntity.status(HttpStatus.FOUND)
                                 .contentType(MediaType.TEXT_XML)
                                 .header(HttpHeaders.LOCATION, authorize)
                                 .build();
        }
        else {
            return ResponseEntity.status(status)
                                 .contentType(mediaType == null ? MediaType.APPLICATION_JSON : mediaType)
                                 .body(R.okData(authorize));
        }
    }
}

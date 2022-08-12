package pro.fessional.wings.slardar.security.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pro.fessional.mirana.func.Dcl;
import pro.fessional.wings.slardar.security.WingsAuthPageHandler;
import pro.fessional.wings.slardar.servlet.request.RequestHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * 如果是 forward，建议使用UNAUTHORIZED，直接访问建议OK
 *
 * @author trydofor
 * @since 2021-02-17
 */
public class ComboWingsAuthPageHandler implements WingsAuthPageHandler {

    private final List<Combo> combos = new ArrayList<>();
    private final Dcl<Void> dclCombos = Dcl.of(() -> combos.sort(Comparator.comparingInt(Combo::getOrder)));

    private final ResponseEntity<?> NOT_FOUND = ResponseEntity.notFound().build();

    @Override
    public ResponseEntity<?> response(@NotNull Enum<?> authType, @Nullable MediaType mediaType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        dclCombos.runIfDirty();
        if (mediaType == null) {
            try {
                mediaType = MediaType.valueOf(request.getContentType());
            }
            catch (Exception e) {
                // ignore
            }
        }
        final HttpStatus status = RequestHelper.isForwarding(request) ? HttpStatus.UNAUTHORIZED : HttpStatus.OK;
        for (Combo combo : combos) {
            ResponseEntity<?> res = combo.response(authType, mediaType, request, response, status);
            if (res != null) return res;
        }
        return NOT_FOUND;
    }

    public void add(Combo source) {
        if (source == null) return;
        combos.add(source);
        dclCombos.setDirty();
    }

    public void addAll(Collection<? extends Combo> source) {
        if (source == null) return;
        combos.addAll(source);
        dclCombos.setDirty();
    }


    public interface Combo extends Ordered {

        /**
         * @param authType  authType
         * @param mediaType 内容类型
         * @param request   request
         * @param response  response
         * @param status    建议的status
         * @return null 如果不能处理
         */
        ResponseEntity<?> response(@NotNull Enum<?> authType, @Nullable MediaType mediaType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull HttpStatus status);
    }
}

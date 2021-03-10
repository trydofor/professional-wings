package pro.fessional.wings.slardar.security.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import pro.fessional.mirana.func.Dcl;
import pro.fessional.wings.slardar.security.WingsAuthPageHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-02-17
 */
public class ComboWingsAuthPageHandler implements WingsAuthPageHandler {

    private final List<Combo> combos = new ArrayList<>();
    private final Dcl dclCombos = Dcl.of(() -> combos.sort(Comparator.comparingInt(Combo::getOrder)));

    private final ResponseEntity<?> NOT_FOUND = ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                              .build();

    @Override
    public ResponseEntity<?> response(@NotNull Enum<?> authType, @Nullable MimeType mimeType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        dclCombos.runIfDirty();
        for (Combo combo : combos) {
            ResponseEntity<?> res = combo.response(authType, mimeType, request, response);
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
         * @param authType authType
         * @param mimeType 内容类型
         * @param request  request
         * @param response response
         * @return null 如果不能处理
         */
        ResponseEntity<?> response(@NotNull Enum<?> authType, @Nullable MimeType mimeType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response);
    }
}

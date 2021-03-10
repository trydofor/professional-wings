package pro.fessional.wings.slardar.servlet.response.view;


import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 忽略Model，直接render view
 *
 * @author trydofor
 * @since 2021-03-10
 */
public abstract class OnlyView<T> extends AbstractView {

    private final String contentType;
    private final T data;

    public OnlyView(String contentType, T data) {
        this.contentType = contentType;
        this.data = data;
    }

    public abstract void responseData(@NotNull T data, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception;

    @Override
    protected void renderMergedOutputModel(@NotNull Map<String, Object> model,
                                           @NotNull HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        response.setContentType(this.contentType);
        if (data != null) {
            responseData(data, request, response);
        }
    }
}

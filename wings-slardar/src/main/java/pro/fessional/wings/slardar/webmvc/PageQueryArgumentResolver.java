package pro.fessional.wings.slardar.webmvc;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.page.PageDefault;
import pro.fessional.mirana.page.PageQuery;

/**
 * @author trydofor
 * @since 2020-12-29
 */
@RequiredArgsConstructor
public class PageQueryArgumentResolver implements HandlerMethodArgumentResolver {

    private final Config config;

    /**
     * 精确匹配PageQuery，避免污染子类
     *
     * @param parameter MethodParameter
     * @return supports
     */
    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return PageQuery.class.equals(parameter.getParameterType());
    }

    @Override
    public PageQuery resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                     @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final PageDefault annotation = parameter.getParameterAnnotation(PageDefault.class);

        int page = config.page;
        int size = config.size;
        String sort = Null.Str;
        String[] pageAlias = config.pageAlias;
        String[] sizeAlias = config.sizeAlias;
        String[] sortAlias = config.sortAlias;
        if (annotation != null) {
            page = Math.max(page, annotation.page());
            size = Math.max(size, annotation.size());
            sort = notEmpty(sort, annotation.sort());
            pageAlias = notEmpty(pageAlias, annotation.pageAlias());
            sizeAlias = notEmpty(sizeAlias, annotation.sizeAlias());
            sortAlias = notEmpty(sortAlias, annotation.sortAlias());
        }

        page = getParameter(page, webRequest, pageAlias);
        size = getParameter(size, webRequest, sizeAlias);
        sort = getParameter(sort, webRequest, sortAlias);

        return new PageQuery(page, size, sort);
    }

    private String getParameter(String df, NativeWebRequest rq, String[] al) {
        for (String s : al) {
            final String p = rq.getParameter(s);
            if (p != null && p.length() > 0) {
                return p;
            }
        }
        return df;
    }

    private int getParameter(int df, NativeWebRequest rq, String[] al) {
        for (String s : al) {
            final String p = rq.getParameter(s);
            if (p != null && p.length() > 0) {
                try {
                    return Integer.parseInt(p);
                } catch (NumberFormatException e) {
                    // continue
                }
            }
        }
        return df;
    }

    private String notEmpty(String df, String s1) {
        if (s1 == null || s1.isEmpty()) {
            return df;
        } else {
            return s1;
        }
    }

    private String[] notEmpty(String[] df, String[] s1) {
        if (s1 != null && s1.length > 0) {
            return s1;
        } else {
            return df;
        }
    }

    @Data
    public static class Config {
        private int page = 1;
        private int size = 20;
        private String[] pageAlias = Null.StrArr;
        private String[] sizeAlias = Null.StrArr;
        private String[] sortAlias = Null.StrArr;
    }
}

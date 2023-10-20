package pro.fessional.wings.slardar.webmvc;

import lombok.Getter;
import lombok.Setter;
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
@Setter
@Getter
public class PageQueryArgumentResolver implements HandlerMethodArgumentResolver {

    private int page = 1;
    private int size = 20;
    private String[] pageAlias = Null.StrArr;
    private String[] sizeAlias = Null.StrArr;
    private String[] sortAlias = Null.StrArr;

    /**
     * Exactly match PageQuery to avoid pollution of subclasses
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
                                     @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        final PageDefault annotation = parameter.getParameterAnnotation(PageDefault.class);

        int pg = page;
        int sz = size;
        String sort = Null.Str;
        String[] pas = pageAlias;
        String[] szs = sizeAlias;
        String[] sta = sortAlias;
        if (annotation != null) {
            pg = Math.max(pg, annotation.page());
            sz = Math.max(sz, annotation.size());
            sort = notEmpty(sort, annotation.sort());
            pas = notEmpty(pas, annotation.pageAlias());
            szs = notEmpty(szs, annotation.sizeAlias());
            sta = notEmpty(sta, annotation.sortAlias());
        }

        pg = getParameter(pg, webRequest, pas);
        sz = getParameter(sz, webRequest, szs);
        sort = getParameter(sort, webRequest, sta);

        return new PageQuery(pg, sz, sort);
    }

    private String getParameter(String df, NativeWebRequest rq, String[] al) {
        for (String s : al) {
            final String p = rq.getParameter(s);
            if (p != null && !p.isEmpty()) {
                return p;
            }
        }
        return df;
    }

    private int getParameter(int df, NativeWebRequest rq, String[] al) {
        for (String s : al) {
            final String p = rq.getParameter(s);
            if (p != null && !p.isEmpty()) {
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
}

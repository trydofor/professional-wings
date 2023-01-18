package pro.fessional.wings.slardar.webmvc;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author trydofor
 * @since 2020-12-25
 */
public class SpringPageHelper {

    @NotNull
    public static PageRequest from(@NotNull PageQuery pq) {
        Sort st = from(pq.getSort());
        return PageRequest.of(pq.getPage(), pq.getSize(), st);
    }

    @NotNull
    public static Sort from(String sort) {
        if (sort == null || sort.isEmpty()) return Sort.unsorted();
        final List<PageUtil.By> bys = PageUtil.sort(sort);
        List<Sort.Order> ods = new ArrayList<>(bys.size());
        for (PageUtil.By by : bys) {
            if (by.asc) {
                ods.add(Sort.Order.asc(by.key));
            } else {
                ods.add(Sort.Order.desc(by.key));
            }
        }
        return Sort.by(ods);
    }

    @NotNull
    public static PageQuery into(@NotNull Pageable pageable) {
        return new PageQuery()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .setSort(into(pageable.getSort()));
    }

    @NotNull
    public static String into(Sort sort) {
        if (sort == null) return Null.Str;
        final PageUtil.Sb sb = PageUtil.sortBy();
        for (Sort.Order od : sort) {
            sb.by(od.getProperty(), od.isAscending());
        }
        return sb.build();
    }
}

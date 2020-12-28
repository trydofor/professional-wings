package pro.fessional.wings.faceless.database;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageUtil;

/**
 * @author trydofor
 * @since 2020-12-25
 */
public class SpringPageHelper extends WingsPageHelper {
    public static PageQuery with(Pageable pageable) {
        final PageQuery pq = new PageQuery(pageable.getPageNumber(), pageable.getPageSize());
        final Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            final PageUtil.Sb sb = PageUtil.sort();
            for (Sort.Order od : sort) {
                sb.by(od.getProperty(), od.isAscending());
            }
            pq.setSortBy(sb.build());
        }
        return pq;
    }
}

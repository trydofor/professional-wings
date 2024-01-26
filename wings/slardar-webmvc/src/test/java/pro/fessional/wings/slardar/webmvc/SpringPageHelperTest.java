package pro.fessional.wings.slardar.webmvc;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pro.fessional.mirana.page.PageQuery;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-12-29
 */
public class SpringPageHelperTest {

    @Test
    @TmsLink("C13113")
    void pageRequest2Query() {
        PageQuery pq = new PageQuery(2, 3, "id,-name");
        final PageRequest pr = SpringPageHelper.from(pq);
        final PageQuery ps = SpringPageHelper.into(pr);
        assertEquals(pq, ps);
    }

    @Test
    @TmsLink("C13114")
    void pageQuery2Request() {
        final List<Sort.Order> ods = Arrays.asList(Sort.Order.asc("id"), Sort.Order.desc("name"));
        PageRequest pr = PageRequest.of(3, 4, Sort.by(ods));
        final PageQuery pq = SpringPageHelper.into(pr);
        final PageRequest ps = SpringPageHelper.from(pq);
        assertEquals(pr, ps);
    }
}

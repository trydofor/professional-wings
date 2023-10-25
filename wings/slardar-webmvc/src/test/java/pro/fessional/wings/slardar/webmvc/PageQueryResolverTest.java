package pro.fessional.wings.slardar.webmvc;

import io.qameta.allure.TmsLink;
import lombok.Data;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.wings.slardar.app.controller.TestPageQueryController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author trydofor
 * @since 2020-06-03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PageQueryResolverTest {

//    @LocalServerPort
//    private int port;

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;


    @Setter(onMethod_ = {@Autowired})
    private RestTemplate restTemplate;

    @Test
    @TmsLink("C13105")
    public void testModelAttribute() {
        int page = 44;
        int size = 55;
        String sort = "k1,-k2";
        PageQuery pq = restTemplate.getForObject(host + "/test/page-request.html?page=" + page
                + "&size=" + size
                + "&sort=" + sort, PageQuery.class);
        assertNotNull(pq);
        assertEquals(page, pq.getPage());
        assertEquals(size, pq.getSize());
        assertEquals(sort, pq.getSort());
    }

    @Test
    @TmsLink("C13106")
    public void testPageQuery() {
        int page = 44;
        int size = 55;
        String sort = "k1,-k2";
        PageQuery pq = restTemplate.getForObject(host + "/test/page-request-0.html?page=" + page
                + "&size=" + size
                + "&sort=" + sort, PageQuery.class);
        assertNotNull(pq);
        assertEquals(page, pq.getPage());
        assertEquals(size, pq.getSize());
        assertEquals(sort, pq.getSort());
    }

    @Test
    @TmsLink("C13107")
    public void testPageDefault1() {
        int page = 33;
        int size = 44;
        String sort = "k1,-k2";
        PageQuery pq = restTemplate.getForObject(host + "/test/page-request-1.html?page=" + page
                + "&size=" + size
                + "&sort=" + sort, PageQuery.class);
        assertNotNull(pq);
        assertEquals(page, pq.getPage());
        assertEquals(size, pq.getSize());
        assertEquals(sort, pq.getSort());
    }
    @Test
    @TmsLink("C13108")
    public void testPageDefault2() {
        int page = 33;
        int size = 44;
        String sort = "k1,-k2";
        PageQuery pq = restTemplate.getForObject(host + "/test/page-request-1.html?pageNumber=" + page
                + "&pageSize=" + size
                + "&sortBy=" + sort, PageQuery.class);
        assertNotNull(pq);
        assertEquals(page, pq.getPage());
        assertEquals(size, pq.getSize());
        assertEquals(sort, pq.getSort());
    }

    @Test
    @TmsLink("C13109")
    public void testPageDefault3() {
        String sort = "k1,-k2";
        PageQuery pq = restTemplate.getForObject(host + "/test/page-request-2.html?sb=" + sort, PageQuery.class);
        assertNotNull(pq);
        assertEquals(2, pq.getPage());
        assertEquals(22, pq.getSize());
        assertEquals(sort, pq.getSort());
    }

    @Test
    @TmsLink("C13110")
    public void testPageBody3() {
        TestPageQueryController.Ins ins = new TestPageQueryController.Ins();
        ins.setPage(2);
        ins.setSize(22);
        ins.setName("name");
        ins.setSort("k1,-k2");
        TestPageQueryController.Ins pq = restTemplate.postForObject(host + "/test/page-request-3.html",ins, TestPageQueryController.Ins.class);
        assertEquals(pq, ins);
    }

    @Data
    public static class Is{
        private String name;
    }
    @Test
    @TmsLink("C13111")
    public void testPageBody4() {
        Is is = new Is();
        is.setName("name");

        TestPageQueryController.Ins pq = restTemplate.postForObject(host + "/test/page-request-4.html", is, TestPageQueryController.Ins.class);
        TestPageQueryController.Ins ins = new TestPageQueryController.Ins();
        ins.setName("name");
        ins.setPage(2);
        ins.setSize(22);
        assertNotEquals(pq, ins);
    }
}

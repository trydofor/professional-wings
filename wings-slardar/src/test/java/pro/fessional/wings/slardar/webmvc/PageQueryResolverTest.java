package pro.fessional.wings.slardar.webmvc;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import pro.fessional.mirana.page.PageQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void testPageDefault3() {
        String sort = "k1,-k2";
        PageQuery pq = restTemplate.getForObject(host + "/test/page-request-2.html?sb=" + sort, PageQuery.class);
        assertNotNull(pq);
        assertEquals(2, pq.getPage());
        assertEquals(22, pq.getSize());
        assertEquals(sort, pq.getSort());
    }
}

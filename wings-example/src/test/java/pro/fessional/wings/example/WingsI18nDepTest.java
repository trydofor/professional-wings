package pro.fessional.wings.example;

import lombok.Setter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * @author trydofor
 * @since 2019-06-25
 */

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"debug = true"})
public class WingsI18nDepTest {

    @Setter(onMethod = @__({@Autowired}))
    private MessageSource messageSource;

    @Test
    public void cnEn() {
        String cn = messageSource.getMessage("base.not-empty", new Object[]{"姓名"}, Locale.CHINA);
        String en = messageSource.getMessage("base.not-empty", new Object[]{"name"}, Locale.US);
        String jp = messageSource.getMessage("base.not-empty", new Object[]{"name"}, Locale.JAPAN);
        assertEquals("姓名 不能为空2", cn);
        assertEquals("name can not be empty", en);
        assertEquals("name ここを空栏にしてはいけません。", jp);
    }
}
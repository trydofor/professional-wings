package pro.fessional.wings.silencer.info;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author trydofor
 * @since 2021-06-02
 */
@SpringBootTest
@Disabled("maven build")
public class InfoPrintTest {

    @Setter(onMethod_ = {@Autowired})
    private BuildProperties buildProperties;

    @Setter(onMethod_ = {@Autowired})
    private GitProperties gitProperties;

    @Test
    public void printTest(){
        System.out.println(buildProperties);
        System.out.println(gitProperties);
    }
}

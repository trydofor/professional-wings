package pro.fessional.wings.silencer.scanner.noscan;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author trydofor
 * @since 2023-10-27
 */
@Component
@Data
public class TestBeanNoScan {
    private String name = "me";
}

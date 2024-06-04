package pro.fessional.wings.silencer.app.service.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pro.fessional.wings.silencer.app.service.TestReorderService;

/**
 * @author trydofor
 * @since 2024-05-11
 */
@Service
@Order(2)
public class TestReorderServiceImpl2 implements TestReorderService {

    @Override
    public int getOrder() {
        return 1;
    }
    @Override
    public String toString() {
        return "2";
    }
}

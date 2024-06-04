package pro.fessional.wings.silencer.app.service.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pro.fessional.wings.silencer.app.service.TestReorderService;

/**
 * @author trydofor
 * @since 2024-05-11
 */
@Service
@Order(1)
public class TestReorderServiceImpl1 implements TestReorderService {

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public String toString() {
        return "1";
    }
}

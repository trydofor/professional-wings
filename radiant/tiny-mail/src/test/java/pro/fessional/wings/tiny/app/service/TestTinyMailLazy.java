package pro.fessional.wings.tiny.app.service;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import pro.fessional.wings.tiny.mail.service.TinyMailLazy;

import java.util.HashSet;

/**
 * @author trydofor
 * @since 2024-07-12
 */
@Service
public class TestTinyMailLazy implements TinyMailLazy {

    private final HashSet<String> exception1st = new HashSet<>();

    @Override
    public @Nullable Edit lazyEdit(@Nullable String para) {
        if(para == null) return null;

        if (exception1st.add(para)) {
            if (para.contains("RuntimeException")) {
                throw new RuntimeException("Mock "+ para);
            }
        }

        Edit edit = new Edit();
        edit.setContent(para);
        return edit;
    }
}

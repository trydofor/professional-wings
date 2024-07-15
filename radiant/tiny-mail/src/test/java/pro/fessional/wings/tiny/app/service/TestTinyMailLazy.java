package pro.fessional.wings.tiny.app.service;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;
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
        if (para == null) return null;
        String txt = FastJsonHelper.object(para, String.class);
        if (exception1st.add(txt)) {
            if (para.contains("RuntimeException")) {
                throw new RuntimeException("Mock " + txt);
            }
        }

        Edit edit = new Edit();
        edit.setContent(txt);
        return edit;
    }
}

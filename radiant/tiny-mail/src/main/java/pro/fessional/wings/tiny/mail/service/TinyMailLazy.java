package pro.fessional.wings.tiny.mail.service;

import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.Resource;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;

import java.util.Map;

/**
 * @author trydofor
 * @since 2024-07-12
 */
public interface TinyMailLazy {

    /**
     * get the registered name of bean, for safe reason
     */
    default String lazyBean() {
        return this.getClass().getName();
    }

    default String lazyPara(@Nullable Object para) {
        return para == null ? null : FastJsonHelper.string(para);
    }

    /**
     * use lazyPara to edit the lazy mail if get nonnull item.
     * stop sending if get exception or all null items
     */
    @Nullable
    Edit lazyEdit(@Nullable String para);

    @Data
    class Edit {
        private String subject = null;
        private String content = null;
        private Map<String, Resource> attachment = null;
        private Boolean html;
    }
}

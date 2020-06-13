package pro.fessional.wings.faceless.service.wini18n;

import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.faceless.enums.StandardI18nEnum;
import pro.fessional.wings.faceless.enums.StandardLanguageEnum;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2020-06-11
 */
public interface StandardI18nService {

    /**
     * 重新加载所有数据
     *
     * @return 加载的数量
     */
    int reloadAll();

    /**
     * 按base重新加载
     *
     * @param base base
     * @return 加载的数量
     */
    int reloadBase(String base);

    @Nullable
    String load(String base, String kind, String ukey, Locale lang);

    @Nullable
    default String load(StandardI18nEnum enu, StandardLanguageEnum lang) {
        if (enu == null || lang == null) return null;
        return load(enu, lang.toLocale());
    }

    @Nullable
    default String load(StandardI18nEnum enu, Locale lang) {
        if (enu == null || lang == null) return null;
        return load(enu.getBase(), enu.getKind(), enu.getUkey(), lang);
    }
}

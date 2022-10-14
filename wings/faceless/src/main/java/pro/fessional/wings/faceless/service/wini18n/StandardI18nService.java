package pro.fessional.wings.faceless.service.wini18n;

import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.data.CodeEnum;
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

    /**
     * 同数据表 sys_standard_i18n 对应
     *
     * @param base 基点，表名或java包名
     * @param kind 种类，字段或java类名
     * @param ukey 键值，唯一性值，如id.###|type.code|enum
     * @param lang 语言
     * @return 多国语
     */
    @Nullable
    String load(String base, String kind, String ukey, Locale lang);

    /**
     * 使用I18nEnum的package，className，name获取I18n
     *
     * @param enu  I18nEnum
     * @param lang locale
     * @return i18n message
     */
    @Nullable
    default String load(StandardI18nEnum enu, StandardLanguageEnum lang) {
        if (enu == null || lang == null) return null;
        return load(enu, lang.toLocale());
    }

    /**
     * 使用I18nEnum的base，kind，ukey获取I18n
     *
     * @param enu  I18nEnum
     * @param lang locale
     * @return i18n message
     */
    @Nullable
    default String load(StandardI18nEnum enu, Locale lang) {
        if (enu == null || lang == null) return null;
        return load(enu.getBase(), enu.getKind(), enu.getUkey(), lang);
    }

    /**
     * 使用Enum的package，className，name获取I18n
     *
     * @param enu  enum
     * @param lang locale
     * @return i18n message
     */
    @Nullable
    default String loadEnum(Enum<?> enu, StandardLanguageEnum lang) {
        if (enu == null || lang == null) return null;
        return loadEnum(enu, lang.toLocale());
    }

    /**
     * 使用Enum的package，className，name获取I18n
     *
     * @param enu  enum
     * @param lang locale
     * @return i18n message
     */
    @Nullable
    default String loadEnum(Enum<?> enu, Locale lang) {
        if (enu == null || lang == null) return null;
        final Class<?> clz = enu.getDeclaringClass();
        final String base = clz.getPackageName();
        final String kind = clz.getName().substring(base.length() + 1);
        return load(base, kind, enu.name(), lang);
    }

    /**
     * 使用CodeEnum的package，className，getCode获取I18n
     *
     * @param enu  CodeEnum
     * @param lang locale
     * @return i18n message
     */
    @Nullable
    default String loadCode(CodeEnum enu, StandardLanguageEnum lang) {
        if (enu == null || lang == null) return null;
        return loadCode(enu, lang.toLocale());
    }

    /**
     * 使用CodeEnum的package，className，getCode获取I18n
     *
     * @param enu  CodeEnum
     * @param lang locale
     * @return i18n message
     */
    @Nullable
    default String loadCode(CodeEnum enu, Locale lang) {
        if (enu == null || lang == null) return null;
        final Class<?> clz = enu.getClass();
        final String base = clz.getPackageName();
        final String kind = clz.getName().substring(base.length() + 1);
        return load(base, kind, enu.getCode(), lang);
    }
}

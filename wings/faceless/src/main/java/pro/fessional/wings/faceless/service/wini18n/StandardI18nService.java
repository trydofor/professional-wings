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

    @Nullable
    String load(String base, String kind, String ukey, Locale lang);

    /**
     * 使用I18nEnum的package，className，name获取I18n
     *
     * @param enu  I18nEnum
     * @param lang locale
     * @return i18n messge
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
     * @return i18n messge
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
     * @return i18n messge
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
     * @return i18n messge
     */
    @Nullable
    default String loadEnum(Enum<?> enu, Locale lang) {
        if (enu == null || lang == null) return null;
        final Class<?> clz = enu.getClass();
        final String pkg = clz.getPackageName();
        final String sn = clz.getName();
        return load(pkg, sn.substring(pkg.length() + 1), enu.name(), lang);
    }

    /**
     * 使用CodeEnum的package，className，getCode获取I18n
     *
     * @param enu  CodeEnum
     * @param lang locale
     * @return i18n messge
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
     * @return i18n messge
     */
    @Nullable
    default String loadCode(CodeEnum enu, Locale lang) {
        if (enu == null || lang == null) return null;
        final Class<?> clz = enu.getClass();
        final String pkg = clz.getPackageName();
        final String sn = clz.getName();
        return load(pkg, sn.substring(pkg.length() + 1), enu.getCode(), lang);
    }
}

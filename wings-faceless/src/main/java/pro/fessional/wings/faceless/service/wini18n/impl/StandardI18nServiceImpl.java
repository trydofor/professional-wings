package pro.fessional.wings.faceless.service.wini18n.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record1;
import pro.fessional.mirana.i18n.LocaleResolver;
import pro.fessional.wings.faceless.database.autogen.tables.SysStandardI18nTable;
import pro.fessional.wings.faceless.database.autogen.tables.daos.SysStandardI18nDao;
import pro.fessional.wings.faceless.database.autogen.tables.pojos.SysStandardI18n;
import pro.fessional.wings.faceless.service.wini18n.StandardI18nService;
import pro.fessional.wings.silencer.message.CombinableMessageSource;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2020-06-13
 */
@RequiredArgsConstructor
public class StandardI18nServiceImpl implements StandardI18nService {

    private final SysStandardI18nDao sysStandardI18nDao;
    private final CombinableMessageSource combinableMessageSource;

    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    @Override
    public int reloadAll() {
        List<SysStandardI18n> pos = sysStandardI18nDao.findAll();
        return cache(pos);

    }

    @Override
    public int reloadBase(String base) {
        SysStandardI18nTable tbl = sysStandardI18nDao.getTable();
        List<SysStandardI18n> pos = sysStandardI18nDao.fetch(tbl.Base, base);
        return cache(pos);
    }

    @Override
    public @Nullable String load(String base, String kind, String ukey, Locale lang) {
        String lan = lang.getLanguage() + "_" + lang.getCountry();
        String key = key(base, kind, ukey, lan);
        return cache.computeIfAbsent(key, s -> {
            SysStandardI18nTable tbl = sysStandardI18nDao.getTable();
            Record1<String> rc1 = sysStandardI18nDao
                    .ctx()
                    .select(tbl.Text)
                    .from(tbl)
                    .where(tbl.Base.eq(base))
                    .and(tbl.Kind.eq(kind))
                    .and(tbl.Ukey.eq(ukey))
                    .and(tbl.Lang.eq(lan))
                    .fetchOne();

            String text = rc1 == null ? "" : rc1.value1();
            if (combinableMessageSource != null && !text.isEmpty()) {
                combinableMessageSource.addMessage(code(base, kind, ukey), lang, text);
            }
            return text;
        });
    }

    private int cache(List<SysStandardI18n> pos) {
        for (SysStandardI18n po : pos) {
            String key = key(po.getBase(), po.getKind(), po.getUkey(), po.getLang());
            String text = po.getText();
            cache.put(key, text);
            if (combinableMessageSource != null) {
                String code = code(po.getBase(), po.getKind(), po.getUkey());
                Locale lang = LocaleResolver.locale(po.getLang());
                combinableMessageSource.addMessage(code, lang, text);
            }
        }
        return pos.size();
    }

    private String code(String base, String kind, String ukey) {
        return base + "." + kind + "." + ukey;
    }

    private String key(String base, String kind, String ukey, String lang) {
        return base + "." + kind + "." + ukey + "@" + lang;
    }
}

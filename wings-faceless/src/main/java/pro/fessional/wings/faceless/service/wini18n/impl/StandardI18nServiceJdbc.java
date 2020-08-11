package pro.fessional.wings.faceless.service.wini18n.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import pro.fessional.mirana.i18n.LocaleResolver;
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
public class StandardI18nServiceJdbc implements StandardI18nService {

    private final JdbcTemplate jdbcTemplate;
    private final CombinableMessageSource combinableMessageSource;

    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    @Override
    public int reloadAll() {
        List<Po> pos = jdbcTemplate.query(
                "SELECT base,kind,ukey,lang,text FROM sys_standard_i18n",
                poMapper);
        return cache(pos);
    }

    @Override
    public int reloadBase(String base) {
        List<Po> pos = jdbcTemplate.query(
                "SELECT base,kind,ukey,lang,text FROM sys_standard_i18n WHERE base=?",
                poMapper,
                base);
        return cache(pos);
    }

    @Override
    public @Nullable String load(String base, String kind, String ukey, Locale lang) {
        String lan = lang.getLanguage() + "_" + lang.getCountry();
        String key = key(base, kind, ukey, lan);
        return cache.computeIfAbsent(key, s -> {
            String txt = jdbcTemplate.query(
                    "SELECT text FROM sys_standard_i18n WHERE base=? AND kind=? AND ukey=? AND lang=?",
                    strExtractor,
                    base, kind, ukey, lan);
            String text = txt == null ? "" : txt;
            if (combinableMessageSource != null && !text.isEmpty()) {
                combinableMessageSource.addMessage(code(base, kind, ukey), lang, text);
            }
            return text;
        });
    }

    private int cache(List<Po> pos) {
        for (Po po : pos) {
            String key = key(po.base, po.kind, po.ukey, po.lang);
            String txt = po.text;
            cache.put(key, txt);
            if (combinableMessageSource != null) {
                String code = code(po.base, po.kind, po.ukey);
                Locale lang = LocaleResolver.locale(po.lang);
                combinableMessageSource.addMessage(code, lang, txt);
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

    private static final ResultSetExtractor<String> strExtractor = rs -> rs.next() ? rs.getString(1) : "";

    private static final RowMapper<Po> poMapper = (rs, rowNum) -> {
        Po po = new Po();
        po.base = rs.getString("base");
        po.kind = rs.getString("kind");
        po.ukey = rs.getString("ukey");
        po.lang = rs.getString("lang");
        po.text = rs.getString("text");
        return po;
    };

    private static class Po {
        private String base = "";
        private String kind = "";
        private String ukey = "";
        private String lang = "";
        private String text = "";
    }
}

package pro.fessional.wings.faceless.service.wini18n.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import pro.fessional.mirana.i18n.LocaleResolver;
import pro.fessional.wings.faceless.database.helper.JdbcTemplateHelper;
import pro.fessional.wings.faceless.service.wini18n.StandardI18nService;
import pro.fessional.wings.silencer.message.MessageSourceHelper;

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
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    @Override
    public int reloadAll() {
        List<Po> pos = jdbcTemplate.query(
                "SELECT base, kind, ukey, lang, hint FROM sys_standard_i18n",
                poMapper);
        return cache(pos);
    }

    @Override
    public int reloadBase(String base) {
        List<Po> pos = jdbcTemplate.query(
                "SELECT base, kind, ukey, lang, hint FROM sys_standard_i18n WHERE base=?",
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
                    "SELECT hint FROM sys_standard_i18n WHERE base=? AND kind=? AND ukey=? AND lang=?",
                    JdbcTemplateHelper.FirstStringOrNull,
                    base, kind, ukey, lan);
            String hint = txt == null ? "" : txt;
            if (!hint.isEmpty()) {
                MessageSourceHelper.Combine.addMessage(code(base, kind, ukey), lang, hint);
            }
            return hint;
        });
    }

    private int cache(List<Po> pos) {
        for (Po po : pos) {
            String key = key(po.base, po.kind, po.ukey, po.lang);
            String txt = po.hint;
            cache.put(key, txt);

            String code = code(po.base, po.kind, po.ukey);
            Locale lang = LocaleResolver.locale(po.lang);
            MessageSourceHelper.Combine.addMessage(code, lang, txt);
        }
        return pos.size();
    }

    private String code(String base, String kind, String ukey) {
        return base + "." + kind + "." + ukey;
    }

    private String key(String base, String kind, String ukey, String lang) {
        return base + "." + kind + "." + ukey + "@" + lang;
    }

    private static final RowMapper<Po> poMapper = (rs, rowNum) -> {
        Po po = new Po();
        po.base = rs.getString("base");
        po.kind = rs.getString("kind");
        po.ukey = rs.getString("ukey");
        po.lang = rs.getString("lang");
        po.hint = rs.getString("hint");
        return po;
    };

    private static class Po {
        private String base = "";
        private String kind = "";
        private String ukey = "";
        private String lang = "";
        private String hint = "";
    }
}

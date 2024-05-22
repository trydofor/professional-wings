/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.faceless.app.database.autogen.tables.pojos;


import pro.fessional.wings.faceless.app.database.autogen.tables.interfaces.ISysStandardI18n;

import javax.annotation.processing.Generated;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;


/**
 * The table <code>wings_faceless.sys_standard_i18n</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9",
        "schema version:2022060102"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class SysStandardI18n implements ISysStandardI18n {

    private static final long serialVersionUID = 1L;

    private String base;
    private String kind;
    private String ukey;
    private String lang;
    private String hint;

    public SysStandardI18n() {}

    public SysStandardI18n(ISysStandardI18n value) {
        this.base = value.getBase();
        this.kind = value.getKind();
        this.ukey = value.getUkey();
        this.lang = value.getLang();
        this.hint = value.getHint();
    }

    public SysStandardI18n(
        String base,
        String kind,
        String ukey,
        String lang,
        String hint
    ) {
        this.base = base;
        this.kind = kind;
        this.ukey = ukey;
        this.lang = lang;
        this.hint = hint;
    }

    /**
     * Getter for <code>sys_standard_i18n.base</code>.
     */
    @Override
    public String getBase() {
        return this.base;
    }

    /**
     * Setter for <code>sys_standard_i18n.base</code>.
     */
    @Override
    public void setBase(String base) {
        this.base = base;
    }

    public void setBaseIf(String base, boolean bool) {
        if (bool) {
            this.base = base;
        }
    }

    public void setBaseIf(Supplier<String> base, boolean bool) {
        if (bool) {
            this.base = base.get();
        }
    }

    public void setBaseIf(String base, Predicate<String> bool) {
        if (bool.test(base)) {
            this.base = base;
        }
    }

    public void setBaseIf(String base, Predicate<String> bool, Supplier<String>... bases) {
        if (bool.test(base)) {
            this.base = base;
            return;
        }
        for (Supplier<String> supplier : bases) {
            base = supplier.get();
            if (bool.test(base)) {
                this.base = base;
                return;
            }
        }
    }

    public void setBaseIfNot(String base, Predicate<String> bool) {
        if (!bool.test(base)) {
            this.base = base;
        }
    }

    public void setBaseIfNot(String base, Predicate<String> bool, Supplier<String>... bases) {
        if (!bool.test(base)) {
            this.base = base;
            return;
        }
        for (Supplier<String> supplier : bases) {
            base = supplier.get();
            if (!bool.test(base)) {
                this.base = base;
                return;
            }
        }
    }

    public void setBaseIf(UnaryOperator<String> base) {
        this.base = base.apply(this.base);
    }


    /**
     * Getter for <code>sys_standard_i18n.kind</code>.
     */
    @Override
    public String getKind() {
        return this.kind;
    }

    /**
     * Setter for <code>sys_standard_i18n.kind</code>.
     */
    @Override
    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setKindIf(String kind, boolean bool) {
        if (bool) {
            this.kind = kind;
        }
    }

    public void setKindIf(Supplier<String> kind, boolean bool) {
        if (bool) {
            this.kind = kind.get();
        }
    }

    public void setKindIf(String kind, Predicate<String> bool) {
        if (bool.test(kind)) {
            this.kind = kind;
        }
    }

    public void setKindIf(String kind, Predicate<String> bool, Supplier<String>... kinds) {
        if (bool.test(kind)) {
            this.kind = kind;
            return;
        }
        for (Supplier<String> supplier : kinds) {
            kind = supplier.get();
            if (bool.test(kind)) {
                this.kind = kind;
                return;
            }
        }
    }

    public void setKindIfNot(String kind, Predicate<String> bool) {
        if (!bool.test(kind)) {
            this.kind = kind;
        }
    }

    public void setKindIfNot(String kind, Predicate<String> bool, Supplier<String>... kinds) {
        if (!bool.test(kind)) {
            this.kind = kind;
            return;
        }
        for (Supplier<String> supplier : kinds) {
            kind = supplier.get();
            if (!bool.test(kind)) {
                this.kind = kind;
                return;
            }
        }
    }

    public void setKindIf(UnaryOperator<String> kind) {
        this.kind = kind.apply(this.kind);
    }


    /**
     * Getter for <code>sys_standard_i18n.ukey</code>.
     */
    @Override
    public String getUkey() {
        return this.ukey;
    }

    /**
     * Setter for <code>sys_standard_i18n.ukey</code>.
     */
    @Override
    public void setUkey(String ukey) {
        this.ukey = ukey;
    }

    public void setUkeyIf(String ukey, boolean bool) {
        if (bool) {
            this.ukey = ukey;
        }
    }

    public void setUkeyIf(Supplier<String> ukey, boolean bool) {
        if (bool) {
            this.ukey = ukey.get();
        }
    }

    public void setUkeyIf(String ukey, Predicate<String> bool) {
        if (bool.test(ukey)) {
            this.ukey = ukey;
        }
    }

    public void setUkeyIf(String ukey, Predicate<String> bool, Supplier<String>... ukeys) {
        if (bool.test(ukey)) {
            this.ukey = ukey;
            return;
        }
        for (Supplier<String> supplier : ukeys) {
            ukey = supplier.get();
            if (bool.test(ukey)) {
                this.ukey = ukey;
                return;
            }
        }
    }

    public void setUkeyIfNot(String ukey, Predicate<String> bool) {
        if (!bool.test(ukey)) {
            this.ukey = ukey;
        }
    }

    public void setUkeyIfNot(String ukey, Predicate<String> bool, Supplier<String>... ukeys) {
        if (!bool.test(ukey)) {
            this.ukey = ukey;
            return;
        }
        for (Supplier<String> supplier : ukeys) {
            ukey = supplier.get();
            if (!bool.test(ukey)) {
                this.ukey = ukey;
                return;
            }
        }
    }

    public void setUkeyIf(UnaryOperator<String> ukey) {
        this.ukey = ukey.apply(this.ukey);
    }


    /**
     * Getter for <code>sys_standard_i18n.lang</code>.
     */
    @Override
    public String getLang() {
        return this.lang;
    }

    /**
     * Setter for <code>sys_standard_i18n.lang</code>.
     */
    @Override
    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setLangIf(String lang, boolean bool) {
        if (bool) {
            this.lang = lang;
        }
    }

    public void setLangIf(Supplier<String> lang, boolean bool) {
        if (bool) {
            this.lang = lang.get();
        }
    }

    public void setLangIf(String lang, Predicate<String> bool) {
        if (bool.test(lang)) {
            this.lang = lang;
        }
    }

    public void setLangIf(String lang, Predicate<String> bool, Supplier<String>... langs) {
        if (bool.test(lang)) {
            this.lang = lang;
            return;
        }
        for (Supplier<String> supplier : langs) {
            lang = supplier.get();
            if (bool.test(lang)) {
                this.lang = lang;
                return;
            }
        }
    }

    public void setLangIfNot(String lang, Predicate<String> bool) {
        if (!bool.test(lang)) {
            this.lang = lang;
        }
    }

    public void setLangIfNot(String lang, Predicate<String> bool, Supplier<String>... langs) {
        if (!bool.test(lang)) {
            this.lang = lang;
            return;
        }
        for (Supplier<String> supplier : langs) {
            lang = supplier.get();
            if (!bool.test(lang)) {
                this.lang = lang;
                return;
            }
        }
    }

    public void setLangIf(UnaryOperator<String> lang) {
        this.lang = lang.apply(this.lang);
    }


    /**
     * Getter for <code>sys_standard_i18n.hint</code>.
     */
    @Override
    public String getHint() {
        return this.hint;
    }

    /**
     * Setter for <code>sys_standard_i18n.hint</code>.
     */
    @Override
    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setHintIf(String hint, boolean bool) {
        if (bool) {
            this.hint = hint;
        }
    }

    public void setHintIf(Supplier<String> hint, boolean bool) {
        if (bool) {
            this.hint = hint.get();
        }
    }

    public void setHintIf(String hint, Predicate<String> bool) {
        if (bool.test(hint)) {
            this.hint = hint;
        }
    }

    public void setHintIf(String hint, Predicate<String> bool, Supplier<String>... hints) {
        if (bool.test(hint)) {
            this.hint = hint;
            return;
        }
        for (Supplier<String> supplier : hints) {
            hint = supplier.get();
            if (bool.test(hint)) {
                this.hint = hint;
                return;
            }
        }
    }

    public void setHintIfNot(String hint, Predicate<String> bool) {
        if (!bool.test(hint)) {
            this.hint = hint;
        }
    }

    public void setHintIfNot(String hint, Predicate<String> bool, Supplier<String>... hints) {
        if (!bool.test(hint)) {
            this.hint = hint;
            return;
        }
        for (Supplier<String> supplier : hints) {
            hint = supplier.get();
            if (!bool.test(hint)) {
                this.hint = hint;
                return;
            }
        }
    }

    public void setHintIf(UnaryOperator<String> hint) {
        this.hint = hint.apply(this.hint);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SysStandardI18n other = (SysStandardI18n) obj;
        if (this.base == null) {
            if (other.base != null)
                return false;
        }
        else if (!this.base.equals(other.base))
            return false;
        if (this.kind == null) {
            if (other.kind != null)
                return false;
        }
        else if (!this.kind.equals(other.kind))
            return false;
        if (this.ukey == null) {
            if (other.ukey != null)
                return false;
        }
        else if (!this.ukey.equals(other.ukey))
            return false;
        if (this.lang == null) {
            if (other.lang != null)
                return false;
        }
        else if (!this.lang.equals(other.lang))
            return false;
        if (this.hint == null) {
            if (other.hint != null)
                return false;
        }
        else if (!this.hint.equals(other.hint))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.base == null) ? 0 : this.base.hashCode());
        result = prime * result + ((this.kind == null) ? 0 : this.kind.hashCode());
        result = prime * result + ((this.ukey == null) ? 0 : this.ukey.hashCode());
        result = prime * result + ((this.lang == null) ? 0 : this.lang.hashCode());
        result = prime * result + ((this.hint == null) ? 0 : this.hint.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SysStandardI18n (");

        sb.append(base);
        sb.append(", ").append(kind);
        sb.append(", ").append(ukey);
        sb.append(", ").append(lang);
        sb.append(", ").append(hint);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(ISysStandardI18n from) {
        setBase(from.getBase());
        setKind(from.getKind());
        setUkey(from.getUkey());
        setLang(from.getLang());
        setHint(from.getHint());
    }

    @Override
    public <E extends ISysStandardI18n> E into(E into) {
        into.from(this);
        return into;
    }
}

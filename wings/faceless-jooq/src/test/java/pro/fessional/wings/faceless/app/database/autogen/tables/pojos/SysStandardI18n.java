/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.faceless.app.database.autogen.tables.pojos;


import pro.fessional.wings.faceless.app.database.autogen.tables.interfaces.ISysStandardI18n;

import javax.annotation.processing.Generated;


/**
 * The table <code>wings.sys_standard_i18n</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.7",
        "schema version:2020102701"
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

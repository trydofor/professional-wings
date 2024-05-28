/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.faceless.app.database.autogen.tables.pojos;


import pro.fessional.wings.faceless.app.database.autogen.tables.interfaces.ISysConstantEnum;

import javax.annotation.processing.Generated;
import java.beans.Transient;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;


/**
 * The table <code>wings_faceless.sys_constant_enum</code>.
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
public class SysConstantEnum implements ISysConstantEnum {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String type;
    private String code;
    private String hint;
    private String info;

    public SysConstantEnum() {}

    public SysConstantEnum(ISysConstantEnum value) {
        this.id = value.getId();
        this.type = value.getType();
        this.code = value.getCode();
        this.hint = value.getHint();
        this.info = value.getInfo();
    }

    public SysConstantEnum(
        Integer id,
        String type,
        String code,
        String hint,
        String info
    ) {
        this.id = id;
        this.type = type;
        this.code = code;
        this.hint = hint;
        this.info = info;
    }

    /**
     * Getter for <code>sys_constant_enum.id</code>.
     */
    @Override
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>sys_constant_enum.id</code>.
     */
    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Transient
    public void setIdIf(Integer id, boolean bool) {
        if (bool) {
            this.id = id;
        }
    }

    @Transient
    public void setIdIf(Supplier<Integer> id, boolean bool) {
        if (bool) {
            this.id = id.get();
        }
    }

    @Transient
    public void setIdIf(Integer id, Predicate<Integer> bool) {
        if (bool.test(id)) {
            this.id = id;
        }
    }

    @Transient
    public void setIdIf(Integer id, Predicate<Integer> bool, Supplier<Integer>... ids) {
        if (bool.test(id)) {
            this.id = id;
            return;
        }
        for (Supplier<Integer> supplier : ids) {
            id = supplier.get();
            if (bool.test(id)) {
                this.id = id;
                return;
            }
        }
    }

    @Transient
    public void setIdIfNot(Integer id, Predicate<Integer> bool) {
        if (!bool.test(id)) {
            this.id = id;
        }
    }

    @Transient
    public void setIdIfNot(Integer id, Predicate<Integer> bool, Supplier<Integer>... ids) {
        if (!bool.test(id)) {
            this.id = id;
            return;
        }
        for (Supplier<Integer> supplier : ids) {
            id = supplier.get();
            if (!bool.test(id)) {
                this.id = id;
                return;
            }
        }
    }

    @Transient
    public void setIdIf(UnaryOperator<Integer> id) {
        this.id = id.apply(this.id);
    }


    /**
     * Getter for <code>sys_constant_enum.type</code>.
     */
    @Override
    public String getType() {
        return this.type;
    }

    /**
     * Setter for <code>sys_constant_enum.type</code>.
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Transient
    public void setTypeIf(String type, boolean bool) {
        if (bool) {
            this.type = type;
        }
    }

    @Transient
    public void setTypeIf(Supplier<String> type, boolean bool) {
        if (bool) {
            this.type = type.get();
        }
    }

    @Transient
    public void setTypeIf(String type, Predicate<String> bool) {
        if (bool.test(type)) {
            this.type = type;
        }
    }

    @Transient
    public void setTypeIf(String type, Predicate<String> bool, Supplier<String>... types) {
        if (bool.test(type)) {
            this.type = type;
            return;
        }
        for (Supplier<String> supplier : types) {
            type = supplier.get();
            if (bool.test(type)) {
                this.type = type;
                return;
            }
        }
    }

    @Transient
    public void setTypeIfNot(String type, Predicate<String> bool) {
        if (!bool.test(type)) {
            this.type = type;
        }
    }

    @Transient
    public void setTypeIfNot(String type, Predicate<String> bool, Supplier<String>... types) {
        if (!bool.test(type)) {
            this.type = type;
            return;
        }
        for (Supplier<String> supplier : types) {
            type = supplier.get();
            if (!bool.test(type)) {
                this.type = type;
                return;
            }
        }
    }

    @Transient
    public void setTypeIf(UnaryOperator<String> type) {
        this.type = type.apply(this.type);
    }


    /**
     * Getter for <code>sys_constant_enum.code</code>.
     */
    @Override
    public String getCode() {
        return this.code;
    }

    /**
     * Setter for <code>sys_constant_enum.code</code>.
     */
    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Transient
    public void setCodeIf(String code, boolean bool) {
        if (bool) {
            this.code = code;
        }
    }

    @Transient
    public void setCodeIf(Supplier<String> code, boolean bool) {
        if (bool) {
            this.code = code.get();
        }
    }

    @Transient
    public void setCodeIf(String code, Predicate<String> bool) {
        if (bool.test(code)) {
            this.code = code;
        }
    }

    @Transient
    public void setCodeIf(String code, Predicate<String> bool, Supplier<String>... codes) {
        if (bool.test(code)) {
            this.code = code;
            return;
        }
        for (Supplier<String> supplier : codes) {
            code = supplier.get();
            if (bool.test(code)) {
                this.code = code;
                return;
            }
        }
    }

    @Transient
    public void setCodeIfNot(String code, Predicate<String> bool) {
        if (!bool.test(code)) {
            this.code = code;
        }
    }

    @Transient
    public void setCodeIfNot(String code, Predicate<String> bool, Supplier<String>... codes) {
        if (!bool.test(code)) {
            this.code = code;
            return;
        }
        for (Supplier<String> supplier : codes) {
            code = supplier.get();
            if (!bool.test(code)) {
                this.code = code;
                return;
            }
        }
    }

    @Transient
    public void setCodeIf(UnaryOperator<String> code) {
        this.code = code.apply(this.code);
    }


    /**
     * Getter for <code>sys_constant_enum.hint</code>.
     */
    @Override
    public String getHint() {
        return this.hint;
    }

    /**
     * Setter for <code>sys_constant_enum.hint</code>.
     */
    @Override
    public void setHint(String hint) {
        this.hint = hint;
    }

    @Transient
    public void setHintIf(String hint, boolean bool) {
        if (bool) {
            this.hint = hint;
        }
    }

    @Transient
    public void setHintIf(Supplier<String> hint, boolean bool) {
        if (bool) {
            this.hint = hint.get();
        }
    }

    @Transient
    public void setHintIf(String hint, Predicate<String> bool) {
        if (bool.test(hint)) {
            this.hint = hint;
        }
    }

    @Transient
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

    @Transient
    public void setHintIfNot(String hint, Predicate<String> bool) {
        if (!bool.test(hint)) {
            this.hint = hint;
        }
    }

    @Transient
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

    @Transient
    public void setHintIf(UnaryOperator<String> hint) {
        this.hint = hint.apply(this.hint);
    }


    /**
     * Getter for <code>sys_constant_enum.info</code>.
     */
    @Override
    public String getInfo() {
        return this.info;
    }

    /**
     * Setter for <code>sys_constant_enum.info</code>.
     */
    @Override
    public void setInfo(String info) {
        this.info = info;
    }

    @Transient
    public void setInfoIf(String info, boolean bool) {
        if (bool) {
            this.info = info;
        }
    }

    @Transient
    public void setInfoIf(Supplier<String> info, boolean bool) {
        if (bool) {
            this.info = info.get();
        }
    }

    @Transient
    public void setInfoIf(String info, Predicate<String> bool) {
        if (bool.test(info)) {
            this.info = info;
        }
    }

    @Transient
    public void setInfoIf(String info, Predicate<String> bool, Supplier<String>... infos) {
        if (bool.test(info)) {
            this.info = info;
            return;
        }
        for (Supplier<String> supplier : infos) {
            info = supplier.get();
            if (bool.test(info)) {
                this.info = info;
                return;
            }
        }
    }

    @Transient
    public void setInfoIfNot(String info, Predicate<String> bool) {
        if (!bool.test(info)) {
            this.info = info;
        }
    }

    @Transient
    public void setInfoIfNot(String info, Predicate<String> bool, Supplier<String>... infos) {
        if (!bool.test(info)) {
            this.info = info;
            return;
        }
        for (Supplier<String> supplier : infos) {
            info = supplier.get();
            if (!bool.test(info)) {
                this.info = info;
                return;
            }
        }
    }

    @Transient
    public void setInfoIf(UnaryOperator<String> info) {
        this.info = info.apply(this.info);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SysConstantEnum other = (SysConstantEnum) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.type == null) {
            if (other.type != null)
                return false;
        }
        else if (!this.type.equals(other.type))
            return false;
        if (this.code == null) {
            if (other.code != null)
                return false;
        }
        else if (!this.code.equals(other.code))
            return false;
        if (this.hint == null) {
            if (other.hint != null)
                return false;
        }
        else if (!this.hint.equals(other.hint))
            return false;
        if (this.info == null) {
            if (other.info != null)
                return false;
        }
        else if (!this.info.equals(other.info))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result + ((this.code == null) ? 0 : this.code.hashCode());
        result = prime * result + ((this.hint == null) ? 0 : this.hint.hashCode());
        result = prime * result + ((this.info == null) ? 0 : this.info.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SysConstantEnum (");

        sb.append(id);
        sb.append(", ").append(type);
        sb.append(", ").append(code);
        sb.append(", ").append(hint);
        sb.append(", ").append(info);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(ISysConstantEnum from) {
        setId(from.getId());
        setType(from.getType());
        setCode(from.getCode());
        setHint(from.getHint());
        setInfo(from.getInfo());
    }

    @Override
    public <E extends ISysConstantEnum> E into(E into) {
        into.from(this);
        return into;
    }
}

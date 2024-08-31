/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.pojos;


import pro.fessional.wings.warlock.database.autogen.tables.interfaces.IWinConfRuntime;

import javax.annotation.processing.Generated;
import java.beans.Transient;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;


/**
 * The table <code>wings.win_conf_runtime</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9",
        "schema version:2020102701"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class WinConfRuntime implements IWinConfRuntime {

    private static final long serialVersionUID = 1L;

    private String key;
    private Boolean enabled;
    private String current;
    private String previous;
    private String initial;
    private String outline;
    private String comment;
    private String handler;

    public WinConfRuntime() {}

    public WinConfRuntime(IWinConfRuntime value) {
        this.key = value.getKey();
        this.enabled = value.getEnabled();
        this.current = value.getCurrent();
        this.previous = value.getPrevious();
        this.initial = value.getInitial();
        this.outline = value.getOutline();
        this.comment = value.getComment();
        this.handler = value.getHandler();
    }

    public WinConfRuntime(
        String key,
        Boolean enabled,
        String current,
        String previous,
        String initial,
        String outline,
        String comment,
        String handler
    ) {
        this.key = key;
        this.enabled = enabled;
        this.current = current;
        this.previous = previous;
        this.initial = initial;
        this.outline = outline;
        this.comment = comment;
        this.handler = handler;
    }

    /**
     * Getter for <code>win_conf_runtime.key</code>.
     */
    @Override
    public String getKey() {
        return this.key;
    }

    /**
     * Setter for <code>win_conf_runtime.key</code>.
     */
    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Transient
    public void setKeyIf(String key, boolean bool) {
        if (bool) {
            this.key = key;
        }
    }

    @Transient
    public void setKeyIf(Supplier<String> key, boolean bool) {
        if (bool) {
            this.key = key.get();
        }
    }

    @Transient
    public void setKeyIf(String key, Predicate<String> bool) {
        if (bool.test(key)) {
            this.key = key;
        }
    }

    @Transient
    public void setKeyIf(String key, Predicate<String> bool, Supplier<String>... keys) {
        if (bool.test(key)) {
            this.key = key;
            return;
        }
        for (Supplier<String> supplier : keys) {
            key = supplier.get();
            if (bool.test(key)) {
                this.key = key;
                return;
            }
        }
    }

    @Transient
    public void setKeyIfNot(String key, Predicate<String> bool) {
        if (!bool.test(key)) {
            this.key = key;
        }
    }

    @Transient
    public void setKeyIfNot(String key, Predicate<String> bool, Supplier<String>... keys) {
        if (!bool.test(key)) {
            this.key = key;
            return;
        }
        for (Supplier<String> supplier : keys) {
            key = supplier.get();
            if (!bool.test(key)) {
                this.key = key;
                return;
            }
        }
    }

    @Transient
    public void setKeyIf(UnaryOperator<String> key) {
        this.key = key.apply(this.key);
    }


    /**
     * Getter for <code>win_conf_runtime.enabled</code>.
     */
    @Override
    public Boolean getEnabled() {
        return this.enabled;
    }

    /**
     * Setter for <code>win_conf_runtime.enabled</code>.
     */
    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Transient
    public void setEnabledIf(Boolean enabled, boolean bool) {
        if (bool) {
            this.enabled = enabled;
        }
    }

    @Transient
    public void setEnabledIf(Supplier<Boolean> enabled, boolean bool) {
        if (bool) {
            this.enabled = enabled.get();
        }
    }

    @Transient
    public void setEnabledIf(Boolean enabled, Predicate<Boolean> bool) {
        if (bool.test(enabled)) {
            this.enabled = enabled;
        }
    }

    @Transient
    public void setEnabledIf(Boolean enabled, Predicate<Boolean> bool, Supplier<Boolean>... enableds) {
        if (bool.test(enabled)) {
            this.enabled = enabled;
            return;
        }
        for (Supplier<Boolean> supplier : enableds) {
            enabled = supplier.get();
            if (bool.test(enabled)) {
                this.enabled = enabled;
                return;
            }
        }
    }

    @Transient
    public void setEnabledIfNot(Boolean enabled, Predicate<Boolean> bool) {
        if (!bool.test(enabled)) {
            this.enabled = enabled;
        }
    }

    @Transient
    public void setEnabledIfNot(Boolean enabled, Predicate<Boolean> bool, Supplier<Boolean>... enableds) {
        if (!bool.test(enabled)) {
            this.enabled = enabled;
            return;
        }
        for (Supplier<Boolean> supplier : enableds) {
            enabled = supplier.get();
            if (!bool.test(enabled)) {
                this.enabled = enabled;
                return;
            }
        }
    }

    @Transient
    public void setEnabledIf(UnaryOperator<Boolean> enabled) {
        this.enabled = enabled.apply(this.enabled);
    }


    /**
     * Getter for <code>win_conf_runtime.current</code>.
     */
    @Override
    public String getCurrent() {
        return this.current;
    }

    /**
     * Setter for <code>win_conf_runtime.current</code>.
     */
    @Override
    public void setCurrent(String current) {
        this.current = current;
    }

    @Transient
    public void setCurrentIf(String current, boolean bool) {
        if (bool) {
            this.current = current;
        }
    }

    @Transient
    public void setCurrentIf(Supplier<String> current, boolean bool) {
        if (bool) {
            this.current = current.get();
        }
    }

    @Transient
    public void setCurrentIf(String current, Predicate<String> bool) {
        if (bool.test(current)) {
            this.current = current;
        }
    }

    @Transient
    public void setCurrentIf(String current, Predicate<String> bool, Supplier<String>... currents) {
        if (bool.test(current)) {
            this.current = current;
            return;
        }
        for (Supplier<String> supplier : currents) {
            current = supplier.get();
            if (bool.test(current)) {
                this.current = current;
                return;
            }
        }
    }

    @Transient
    public void setCurrentIfNot(String current, Predicate<String> bool) {
        if (!bool.test(current)) {
            this.current = current;
        }
    }

    @Transient
    public void setCurrentIfNot(String current, Predicate<String> bool, Supplier<String>... currents) {
        if (!bool.test(current)) {
            this.current = current;
            return;
        }
        for (Supplier<String> supplier : currents) {
            current = supplier.get();
            if (!bool.test(current)) {
                this.current = current;
                return;
            }
        }
    }

    @Transient
    public void setCurrentIf(UnaryOperator<String> current) {
        this.current = current.apply(this.current);
    }


    /**
     * Getter for <code>win_conf_runtime.previous</code>.
     */
    @Override
    public String getPrevious() {
        return this.previous;
    }

    /**
     * Setter for <code>win_conf_runtime.previous</code>.
     */
    @Override
    public void setPrevious(String previous) {
        this.previous = previous;
    }

    @Transient
    public void setPreviousIf(String previous, boolean bool) {
        if (bool) {
            this.previous = previous;
        }
    }

    @Transient
    public void setPreviousIf(Supplier<String> previous, boolean bool) {
        if (bool) {
            this.previous = previous.get();
        }
    }

    @Transient
    public void setPreviousIf(String previous, Predicate<String> bool) {
        if (bool.test(previous)) {
            this.previous = previous;
        }
    }

    @Transient
    public void setPreviousIf(String previous, Predicate<String> bool, Supplier<String>... previouss) {
        if (bool.test(previous)) {
            this.previous = previous;
            return;
        }
        for (Supplier<String> supplier : previouss) {
            previous = supplier.get();
            if (bool.test(previous)) {
                this.previous = previous;
                return;
            }
        }
    }

    @Transient
    public void setPreviousIfNot(String previous, Predicate<String> bool) {
        if (!bool.test(previous)) {
            this.previous = previous;
        }
    }

    @Transient
    public void setPreviousIfNot(String previous, Predicate<String> bool, Supplier<String>... previouss) {
        if (!bool.test(previous)) {
            this.previous = previous;
            return;
        }
        for (Supplier<String> supplier : previouss) {
            previous = supplier.get();
            if (!bool.test(previous)) {
                this.previous = previous;
                return;
            }
        }
    }

    @Transient
    public void setPreviousIf(UnaryOperator<String> previous) {
        this.previous = previous.apply(this.previous);
    }


    /**
     * Getter for <code>win_conf_runtime.initial</code>.
     */
    @Override
    public String getInitial() {
        return this.initial;
    }

    /**
     * Setter for <code>win_conf_runtime.initial</code>.
     */
    @Override
    public void setInitial(String initial) {
        this.initial = initial;
    }

    @Transient
    public void setInitialIf(String initial, boolean bool) {
        if (bool) {
            this.initial = initial;
        }
    }

    @Transient
    public void setInitialIf(Supplier<String> initial, boolean bool) {
        if (bool) {
            this.initial = initial.get();
        }
    }

    @Transient
    public void setInitialIf(String initial, Predicate<String> bool) {
        if (bool.test(initial)) {
            this.initial = initial;
        }
    }

    @Transient
    public void setInitialIf(String initial, Predicate<String> bool, Supplier<String>... initials) {
        if (bool.test(initial)) {
            this.initial = initial;
            return;
        }
        for (Supplier<String> supplier : initials) {
            initial = supplier.get();
            if (bool.test(initial)) {
                this.initial = initial;
                return;
            }
        }
    }

    @Transient
    public void setInitialIfNot(String initial, Predicate<String> bool) {
        if (!bool.test(initial)) {
            this.initial = initial;
        }
    }

    @Transient
    public void setInitialIfNot(String initial, Predicate<String> bool, Supplier<String>... initials) {
        if (!bool.test(initial)) {
            this.initial = initial;
            return;
        }
        for (Supplier<String> supplier : initials) {
            initial = supplier.get();
            if (!bool.test(initial)) {
                this.initial = initial;
                return;
            }
        }
    }

    @Transient
    public void setInitialIf(UnaryOperator<String> initial) {
        this.initial = initial.apply(this.initial);
    }


    /**
     * Getter for <code>win_conf_runtime.outline</code>.
     */
    @Override
    public String getOutline() {
        return this.outline;
    }

    /**
     * Setter for <code>win_conf_runtime.outline</code>.
     */
    @Override
    public void setOutline(String outline) {
        this.outline = outline;
    }

    @Transient
    public void setOutlineIf(String outline, boolean bool) {
        if (bool) {
            this.outline = outline;
        }
    }

    @Transient
    public void setOutlineIf(Supplier<String> outline, boolean bool) {
        if (bool) {
            this.outline = outline.get();
        }
    }

    @Transient
    public void setOutlineIf(String outline, Predicate<String> bool) {
        if (bool.test(outline)) {
            this.outline = outline;
        }
    }

    @Transient
    public void setOutlineIf(String outline, Predicate<String> bool, Supplier<String>... outlines) {
        if (bool.test(outline)) {
            this.outline = outline;
            return;
        }
        for (Supplier<String> supplier : outlines) {
            outline = supplier.get();
            if (bool.test(outline)) {
                this.outline = outline;
                return;
            }
        }
    }

    @Transient
    public void setOutlineIfNot(String outline, Predicate<String> bool) {
        if (!bool.test(outline)) {
            this.outline = outline;
        }
    }

    @Transient
    public void setOutlineIfNot(String outline, Predicate<String> bool, Supplier<String>... outlines) {
        if (!bool.test(outline)) {
            this.outline = outline;
            return;
        }
        for (Supplier<String> supplier : outlines) {
            outline = supplier.get();
            if (!bool.test(outline)) {
                this.outline = outline;
                return;
            }
        }
    }

    @Transient
    public void setOutlineIf(UnaryOperator<String> outline) {
        this.outline = outline.apply(this.outline);
    }


    /**
     * Getter for <code>win_conf_runtime.comment</code>.
     */
    @Override
    public String getComment() {
        return this.comment;
    }

    /**
     * Setter for <code>win_conf_runtime.comment</code>.
     */
    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Transient
    public void setCommentIf(String comment, boolean bool) {
        if (bool) {
            this.comment = comment;
        }
    }

    @Transient
    public void setCommentIf(Supplier<String> comment, boolean bool) {
        if (bool) {
            this.comment = comment.get();
        }
    }

    @Transient
    public void setCommentIf(String comment, Predicate<String> bool) {
        if (bool.test(comment)) {
            this.comment = comment;
        }
    }

    @Transient
    public void setCommentIf(String comment, Predicate<String> bool, Supplier<String>... comments) {
        if (bool.test(comment)) {
            this.comment = comment;
            return;
        }
        for (Supplier<String> supplier : comments) {
            comment = supplier.get();
            if (bool.test(comment)) {
                this.comment = comment;
                return;
            }
        }
    }

    @Transient
    public void setCommentIfNot(String comment, Predicate<String> bool) {
        if (!bool.test(comment)) {
            this.comment = comment;
        }
    }

    @Transient
    public void setCommentIfNot(String comment, Predicate<String> bool, Supplier<String>... comments) {
        if (!bool.test(comment)) {
            this.comment = comment;
            return;
        }
        for (Supplier<String> supplier : comments) {
            comment = supplier.get();
            if (!bool.test(comment)) {
                this.comment = comment;
                return;
            }
        }
    }

    @Transient
    public void setCommentIf(UnaryOperator<String> comment) {
        this.comment = comment.apply(this.comment);
    }


    /**
     * Getter for <code>win_conf_runtime.handler</code>.
     */
    @Override
    public String getHandler() {
        return this.handler;
    }

    /**
     * Setter for <code>win_conf_runtime.handler</code>.
     */
    @Override
    public void setHandler(String handler) {
        this.handler = handler;
    }

    @Transient
    public void setHandlerIf(String handler, boolean bool) {
        if (bool) {
            this.handler = handler;
        }
    }

    @Transient
    public void setHandlerIf(Supplier<String> handler, boolean bool) {
        if (bool) {
            this.handler = handler.get();
        }
    }

    @Transient
    public void setHandlerIf(String handler, Predicate<String> bool) {
        if (bool.test(handler)) {
            this.handler = handler;
        }
    }

    @Transient
    public void setHandlerIf(String handler, Predicate<String> bool, Supplier<String>... handlers) {
        if (bool.test(handler)) {
            this.handler = handler;
            return;
        }
        for (Supplier<String> supplier : handlers) {
            handler = supplier.get();
            if (bool.test(handler)) {
                this.handler = handler;
                return;
            }
        }
    }

    @Transient
    public void setHandlerIfNot(String handler, Predicate<String> bool) {
        if (!bool.test(handler)) {
            this.handler = handler;
        }
    }

    @Transient
    public void setHandlerIfNot(String handler, Predicate<String> bool, Supplier<String>... handlers) {
        if (!bool.test(handler)) {
            this.handler = handler;
            return;
        }
        for (Supplier<String> supplier : handlers) {
            handler = supplier.get();
            if (!bool.test(handler)) {
                this.handler = handler;
                return;
            }
        }
    }

    @Transient
    public void setHandlerIf(UnaryOperator<String> handler) {
        this.handler = handler.apply(this.handler);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final WinConfRuntime other = (WinConfRuntime) obj;
        if (this.key == null) {
            if (other.key != null)
                return false;
        }
        else if (!this.key.equals(other.key))
            return false;
        if (this.enabled == null) {
            if (other.enabled != null)
                return false;
        }
        else if (!this.enabled.equals(other.enabled))
            return false;
        if (this.current == null) {
            if (other.current != null)
                return false;
        }
        else if (!this.current.equals(other.current))
            return false;
        if (this.previous == null) {
            if (other.previous != null)
                return false;
        }
        else if (!this.previous.equals(other.previous))
            return false;
        if (this.initial == null) {
            if (other.initial != null)
                return false;
        }
        else if (!this.initial.equals(other.initial))
            return false;
        if (this.outline == null) {
            if (other.outline != null)
                return false;
        }
        else if (!this.outline.equals(other.outline))
            return false;
        if (this.comment == null) {
            if (other.comment != null)
                return false;
        }
        else if (!this.comment.equals(other.comment))
            return false;
        if (this.handler == null) {
            if (other.handler != null)
                return false;
        }
        else if (!this.handler.equals(other.handler))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
        result = prime * result + ((this.enabled == null) ? 0 : this.enabled.hashCode());
        result = prime * result + ((this.current == null) ? 0 : this.current.hashCode());
        result = prime * result + ((this.previous == null) ? 0 : this.previous.hashCode());
        result = prime * result + ((this.initial == null) ? 0 : this.initial.hashCode());
        result = prime * result + ((this.outline == null) ? 0 : this.outline.hashCode());
        result = prime * result + ((this.comment == null) ? 0 : this.comment.hashCode());
        result = prime * result + ((this.handler == null) ? 0 : this.handler.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("WinConfRuntime (");

        sb.append(key);
        sb.append(", ").append(enabled);
        sb.append(", ").append(current);
        sb.append(", ").append(previous);
        sb.append(", ").append(initial);
        sb.append(", ").append(outline);
        sb.append(", ").append(comment);
        sb.append(", ").append(handler);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(IWinConfRuntime from) {
        setKey(from.getKey());
        setEnabled(from.getEnabled());
        setCurrent(from.getCurrent());
        setPrevious(from.getPrevious());
        setInitial(from.getInitial());
        setOutline(from.getOutline());
        setComment(from.getComment());
        setHandler(from.getHandler());
    }

    @Override
    public <E extends IWinConfRuntime> E into(E into) {
        into.from(this);
        return into;
    }
}

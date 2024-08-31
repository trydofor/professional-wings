package pro.fessional.wings.tiny.grow.track;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2024-07-28
 */
@Data
public class TinyTracking {
    protected final long begin;
    protected final String key;
    protected final String ref;

    protected String app;
    @NotNull
    protected Map<String, Object> env = new LinkedHashMap<>();
    @NotNull
    protected Object[] ins = new Object[0];
    protected Object out;
    protected Throwable err;
    protected long elapse;

    protected long userKey;
    protected long userRef;

    protected long dataKey;
    protected long dataRef;
    protected long dataOpt;

    protected String codeKey;
    protected String codeRef;
    protected String codeOpt;

    protected String wordRef;

    /**
     * rule set of Class/String/Pattern
     */
    @NotNull
    protected final Set<Object> omitRule = new HashSet<>();

    public void setIns(Object... ins) {
        this.ins = ins;
    }

    public void addEnv(String key, Object value) {
        env.put(key, value);
    }

    public void addEnv(Map<String, Object> envs) {
        env.putAll(envs);
    }

    /**
     * <pre>
     * support rule,
     * * Class - object is instance of
     * * String - name equals
     * * Pattern - name matches regexp
     * * Collection - any of above type
     * * Object[] - any of above type
     * </pre>
     */
    public void addOmit(Object omit) {
        if (omit == null) {
            return;
        }
        else if (omit instanceof Class<?> clz) {
            omitRule.add(clz);
        }
        else if (omit instanceof String str) {
            if (!str.isEmpty()) omitRule.add(str);
        }
        else if (omit instanceof Pattern ptn) {
            if (!ptn.pattern().isEmpty()) omitRule.add(ptn);
        }
        else if (omit instanceof Collection<?> col) {
            for (Object o : col) addOmit(o);
        }
        else if (omit instanceof Object[] arr) {
            for (Object o : arr) addOmit(o);
        }
    }
}

package pro.fessional.wings.slardar.context;

import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.pain.CodeException;

/**
 * @author trydofor
 * @since 2023-10-2023/10/17
 */
public class TerminalContextException extends CodeException {

    public TerminalContextException(String code) {
        super(code);
    }

    public TerminalContextException(String code, String message) {
        super(code, message);
    }

    public TerminalContextException(CodeEnum code) {
        super(code);
    }

    public TerminalContextException(CodeEnum code, Object... args) {
        super(code, args);
    }
}

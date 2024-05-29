package pro.fessional.wings.slardar.context;

import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.pain.MessageException;

/**
 * @author trydofor
 * @since 2023-10-20
 */
public class TerminalContextException extends MessageException {

    public TerminalContextException(String code) {
        super(TweakStack.current(code, TerminalContextException.class, DefaultStack), code);
    }

    public TerminalContextException(String code, String message) {
        super(TweakStack.current(code, TerminalContextException.class, DefaultStack), code, message);
    }

    public TerminalContextException(CodeEnum code) {
        super(TweakStack.current(code, TerminalContextException.class, DefaultStack), code);
    }

    public TerminalContextException(CodeEnum code, Object... args) {
        super(TweakStack.current(code, TerminalContextException.class, DefaultStack), code, args);
    }
}

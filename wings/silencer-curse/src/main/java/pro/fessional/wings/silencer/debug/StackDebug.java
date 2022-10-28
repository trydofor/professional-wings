package pro.fessional.wings.silencer.debug;

import pro.fessional.mirana.pain.CodeException;

/**
 * @author trydofor
 * @since 2022-10-27
 */
public class StackDebug {

    // global
    public static void debugGlobal(boolean stack) {
        CodeException.adaptGlobalStack(stack);
    }

    public static void resetGlobal() {
        CodeException.resetThreadStack();
    }

    // thread
    public static void debugThread(boolean stack) {
        CodeException.adaptGlobalStack(stack);
    }

    public static void resetThread() {
        CodeException.resetThreadStack();
    }
}

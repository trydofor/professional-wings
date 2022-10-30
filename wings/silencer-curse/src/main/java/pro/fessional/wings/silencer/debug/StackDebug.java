package pro.fessional.wings.silencer.debug;

import static pro.fessional.mirana.pain.CodeException.TweakStack;

/**
 * @author trydofor
 * @since 2022-10-27
 */
public class StackDebug {

    // global
    public static void debugGlobal(boolean stack) {
        TweakStack.tweakGlobal(stack);
    }

    public static void resetGlobal() {
        TweakStack.resetGlobal();
    }

    // thread
    public static void debugThread(boolean stack) {
        TweakStack.tweakThread(stack);
    }

    public static void resetThread() {
        TweakStack.resetThread();
    }
}

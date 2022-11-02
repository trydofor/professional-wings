package pro.fessional.wings.silencer.tweak;

import static pro.fessional.mirana.pain.CodeException.TweakStack;

/**
 * @author trydofor
 * @since 2022-10-27
 */
public class TweakStack {

    // global
    public static void tweakGlobal(boolean stack) {
        TweakStack.tweakGlobal(stack);
    }

    public static void resetGlobal() {
        TweakStack.resetGlobal();
    }

    // thread
    public static void tweakThread(boolean stack) {
        TweakStack.tweakThread(stack);
    }

    public static void resetThread() {
        TweakStack.resetThread();
    }
}

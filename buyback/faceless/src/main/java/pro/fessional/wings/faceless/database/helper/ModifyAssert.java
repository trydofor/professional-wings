package pro.fessional.wings.faceless.database.helper;

import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.text.FormatUtil;

import static pro.fessional.wings.faceless.database.helper.ModifyPredict.isAEqB;
import static pro.fessional.wings.faceless.database.helper.ModifyPredict.isAGeB;
import static pro.fessional.wings.faceless.database.helper.ModifyPredict.isAGtB;
import static pro.fessional.wings.faceless.database.helper.ModifyPredict.isALeB;
import static pro.fessional.wings.faceless.database.helper.ModifyPredict.isALtB;
import static pro.fessional.wings.faceless.database.helper.ModifyPredict.isANeB;

/**
 * @author trydofor
 * @since 2019-09-18
 */
public class ModifyAssert {

    public static boolean one(int a, CodeEnum err) {
        if (isANeB(a, 1)) throw new CodeException(true, err);
        return true;
    }

    public static boolean less(int a, CodeEnum err) {
        if (isAGtB(a, 1)) throw new CodeException(true, err);
        return true;
    }

    public static boolean more(int a, CodeEnum err) {
        if (isALtB(a, 1)) throw new CodeException(true, err);
        return true;
    }
    public static boolean one(int[] a, CodeEnum err) {
        if (isANeB(a, 1)) throw new CodeException(true, err);
        return true;
    }

    public static boolean less(int[] a, CodeEnum err) {
        if (isAGtB(a, 1)) throw new CodeException(true, err);
        return true;
    }

    public static boolean more(int[] a, CodeEnum err) {
        if (isALtB(a, 1)) throw new CodeException(true, err);
        return true;
    }
    //
    public static boolean aEqB(int a, int b, CodeEnum err) {
        if (isANeB(a, b)) throw new CodeException(true, err);
        return true;
    }

    public static boolean aNeB(int a, int b, CodeEnum err) {
        if (isAEqB(a, b)) throw new CodeException(true, err);
        return true;
    }

    public static boolean aGtB(int a, int b, CodeEnum err) {
        if (isALeB(a, b)) throw new CodeException(true, err);
        return true;
    }

    public static boolean aGeB(int a, int b, CodeEnum err) {
        if (isALtB(a, b)) throw new CodeException(true, err);
        return true;
    }

    public static boolean aLtB(int a, int b, CodeEnum err) {
        if (isAGeB(a, b)) throw new CodeException(true, err);
        return true;
    }

    public static boolean aLeB(int a, int b, CodeEnum err) {
        if (isAGtB(a, b)) throw new CodeException(true, err);
        return true;
    }

    public static boolean aEqB(int[] a, int b, CodeEnum err) {
        if (isANeB(a, b)) throw new CodeException(true, err);
        return true;
    }

    public static boolean aNeB(int[] a, int b, CodeEnum err) {
        if (isAEqB(a, b)) throw new CodeException(true, err);
        return true;
    }

    public static boolean aGtB(int[] a, int b, CodeEnum err) {
        if (isALeB(a, b)) throw new CodeException(true, err);
        return true;
    }

    public static boolean aGeB(int[] a, int b, CodeEnum err) {
        if (isALtB(a, b)) throw new CodeException(true, err);
        return true;
    }

    public static boolean aLtB(int[] a, int b, CodeEnum err) {
        if (isAGeB(a, b)) throw new CodeException(true, err);
        return true;
    }

    public static boolean aLeB(int[] a, int b, CodeEnum err) {
        if (isAGtB(a, b)) throw new CodeException(true, err);
        return true;
    }

    //

    public static int one$(int a, CodeEnum err) {
        if (isANeB(a, 1)) throw new CodeException(true, err);
        return a;
    }

    public static int less$(int a, CodeEnum err) {
        if (isAGtB(a, 1)) throw new CodeException(true, err);
        return a;
    }

    public static int more$(int a, CodeEnum err) {
        if (isALtB(a, 1)) throw new CodeException(true, err);
        return a;
    }
    public static int[] one$(int[] a, CodeEnum err) {
        if (isANeB(a, 1)) throw new CodeException(true, err);
        return a;
    }

    public static int[] less$(int[] a, CodeEnum err) {
        if (isAGtB(a, 1)) throw new CodeException(true, err);
        return a;
    }

    public static int[] more$(int[] a, CodeEnum err) {
        if (isALtB(a, 1)) throw new CodeException(true, err);
        return a;
    }
    //
    public static int aEqB$(int a, int b, CodeEnum err) {
        if (isANeB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int aNeB$(int a, int b, CodeEnum err) {
        if (isAEqB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int aGtB$(int a, int b, CodeEnum err) {
        if (isALeB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int aGeB$(int a, int b, CodeEnum err) {
        if (isALtB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int aLtB$(int a, int b, CodeEnum err) {
        if (isAGeB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int aLeB$(int a, int b, CodeEnum err) {
        if (isAGtB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int[] aEqB$(int[] a, int b, CodeEnum err) {
        if (isANeB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int[] aNeB$(int[] a, int b, CodeEnum err) {
        if (isAEqB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int[] aGtB$(int[] a, int b, CodeEnum err) {
        if (isALeB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int[] aGeB$(int[] a, int b, CodeEnum err) {
        if (isALtB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int[] aLtB$(int[] a, int b, CodeEnum err) {
        if (isAGeB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int[] aLeB$(int[] a, int b, CodeEnum err) {
        if (isAGtB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static boolean one(int a, String err) {
        if (isANeB(a, 1)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean less(int a, String err) {
        if (isAGtB(a, 1)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean more(int a, String err) {
        if (isALtB(a, 1)) throw new IllegalStateException(err);
        return true;
    }
    public static boolean one(int[] a, String err) {
        if (isANeB(a, 1)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean less(int[] a, String err) {
        if (isAGtB(a, 1)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean more(int[] a, String err) {
        if (isALtB(a, 1)) throw new IllegalStateException(err);
        return true;
    }
    //
    public static boolean aEqB(int a, int b, String err) {
        if (isANeB(a, b)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean aNeB(int a, int b, String err) {
        if (isAEqB(a, b)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean aGtB(int a, int b, String err) {
        if (isALeB(a, b)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean aGeB(int a, int b, String err) {
        if (isALtB(a, b)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean aLtB(int a, int b, String err) {
        if (isAGeB(a, b)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean aLeB(int a, int b, String err) {
        if (isAGtB(a, b)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean aEqB(int[] a, int b, String err) {
        if (isANeB(a, b)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean aNeB(int[] a, int b, String err) {
        if (isAEqB(a, b)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean aGtB(int[] a, int b, String err) {
        if (isALeB(a, b)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean aGeB(int[] a, int b, String err) {
        if (isALtB(a, b)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean aLtB(int[] a, int b, String err) {
        if (isAGeB(a, b)) throw new IllegalStateException(err);
        return true;
    }

    public static boolean aLeB(int[] a, int b, String err) {
        if (isAGtB(a, b)) throw new IllegalStateException(err);
        return true;
    }

    //

    public static int one$(int a, String err) {
        if (isANeB(a, 1)) throw new IllegalStateException(err);
        return a;
    }

    public static int less$(int a, String err) {
        if (isAGtB(a, 1)) throw new IllegalStateException(err);
        return a;
    }

    public static int more$(int a, String err) {
        if (isALtB(a, 1)) throw new IllegalStateException(err);
        return a;
    }
    public static int[] one$(int[] a, String err) {
        if (isANeB(a, 1)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] less$(int[] a, String err) {
        if (isAGtB(a, 1)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] more$(int[] a, String err) {
        if (isALtB(a, 1)) throw new IllegalStateException(err);
        return a;
    }
    //
    public static int aEqB$(int a, int b, String err) {
        if (isANeB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int aNeB$(int a, int b, String err) {
        if (isAEqB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int aGtB$(int a, int b, String err) {
        if (isALeB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int aGeB$(int a, int b, String err) {
        if (isALtB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int aLtB$(int a, int b, String err) {
        if (isAGeB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int aLeB$(int a, int b, String err) {
        if (isAGtB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] aEqB$(int[] a, int b, String err) {
        if (isANeB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] aNeB$(int[] a, int b, String err) {
        if (isAEqB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] aGtB$(int[] a, int b, String err) {
        if (isALeB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] aGeB$(int[] a, int b, String err) {
        if (isALtB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] aLtB$(int[] a, int b, String err) {
        if (isAGeB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] aLeB$(int[] a, int b, String err) {
        if (isAGtB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static boolean one(int a, String err, Object... arg) {
        if (isANeB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean less(int a, String err, Object... arg) {
        if (isAGtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean more(int a, String err, Object... arg) {
        if (isALtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }
    public static boolean one(int[] a, String err, Object... arg) {
        if (isANeB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean less(int[] a, String err, Object... arg) {
        if (isAGtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean more(int[] a, String err, Object... arg) {
        if (isALtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }
    //
    public static boolean aEqB(int a, int b, String err, Object... arg) {
        if (isANeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean aNeB(int a, int b, String err, Object... arg) {
        if (isAEqB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean aGtB(int a, int b, String err, Object... arg) {
        if (isALeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean aGeB(int a, int b, String err, Object... arg) {
        if (isALtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean aLtB(int a, int b, String err, Object... arg) {
        if (isAGeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean aLeB(int a, int b, String err, Object... arg) {
        if (isAGtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean aEqB(int[] a, int b, String err, Object... arg) {
        if (isANeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean aNeB(int[] a, int b, String err, Object... arg) {
        if (isAEqB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean aGtB(int[] a, int b, String err, Object... arg) {
        if (isALeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean aGeB(int[] a, int b, String err, Object... arg) {
        if (isALtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean aLtB(int[] a, int b, String err, Object... arg) {
        if (isAGeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    public static boolean aLeB(int[] a, int b, String err, Object... arg) {
        if (isAGtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return true;
    }

    //

    public static int one$(int a, String err, Object... arg) {
        if (isANeB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int less$(int a, String err, Object... arg) {
        if (isAGtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int more$(int a, String err, Object... arg) {
        if (isALtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }
    public static int[] one$(int[] a, String err, Object... arg) {
        if (isANeB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] less$(int[] a, String err, Object... arg) {
        if (isAGtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] more$(int[] a, String err, Object... arg) {
        if (isALtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }
    //
    public static int aEqB$(int a, int b, String err, Object... arg) {
        if (isANeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int aNeB$(int a, int b, String err, Object... arg) {
        if (isAEqB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int aGtB$(int a, int b, String err, Object... arg) {
        if (isALeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int aGeB$(int a, int b, String err, Object... arg) {
        if (isALtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int aLtB$(int a, int b, String err, Object... arg) {
        if (isAGeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int aLeB$(int a, int b, String err, Object... arg) {
        if (isAGtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] aEqB$(int[] a, int b, String err, Object... arg) {
        if (isANeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] aNeB$(int[] a, int b, String err, Object... arg) {
        if (isAEqB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] aGtB$(int[] a, int b, String err, Object... arg) {
        if (isALeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] aGeB$(int[] a, int b, String err, Object... arg) {
        if (isALtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] aLtB$(int[] a, int b, String err, Object... arg) {
        if (isAGeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] aLeB$(int[] a, int b, String err, Object... arg) {
        if (isAGtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }
}

package pro.fessional.wings.faceless.database.helper;

import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.text.FormatUtil;

/**
 * @author trydofor
 * @since 2019-09-18
 */
public class DaoAssert {

    public static void assertEq1(int a, CodeEnum err) {
        if (DaoPredict.aNeB(a, 1)) throw new CodeException(true, err);
    }

    public static void assertLe1(int a, CodeEnum err) {
        if (DaoPredict.aGtB(a, 1)) throw new CodeException(true, err);
    }

    public static void assertGe1(int a, CodeEnum err) {
        if (DaoPredict.aLtB(a, 1)) throw new CodeException(true, err);
    }
    public static void assertEq1(int[] a, CodeEnum err) {
        if (DaoPredict.aNeB(a, 1)) throw new CodeException(true, err);
    }

    public static void assertLe1(int[] a, CodeEnum err) {
        if (DaoPredict.aGtB(a, 1)) throw new CodeException(true, err);
    }

    public static void assertGe1(int[] a, CodeEnum err) {
        if (DaoPredict.aLtB(a, 1)) throw new CodeException(true, err);
    }
    //
    public static void assertEqB(int a, int b, CodeEnum err) {
        if (DaoPredict.aNeB(a, b)) throw new CodeException(true, err);
    }

    public static void assertNeB(int a, int b, CodeEnum err) {
        if (DaoPredict.aEqB(a, b)) throw new CodeException(true, err);
    }

    public static void assertGtB(int a, int b, CodeEnum err) {
        if (DaoPredict.aLeB(a, b)) throw new CodeException(true, err);
    }

    public static void assertGeB(int a, int b, CodeEnum err) {
        if (DaoPredict.aLtB(a, b)) throw new CodeException(true, err);
    }

    public static void assertLtB(int a, int b, CodeEnum err) {
        if (DaoPredict.aGeB(a, b)) throw new CodeException(true, err);
    }

    public static void assertLeB(int a, int b, CodeEnum err) {
        if (DaoPredict.aGtB(a, b)) throw new CodeException(true, err);
    }

    public static void assertEqB(int[] a, int b, CodeEnum err) {
        if (DaoPredict.aNeB(a, b)) throw new CodeException(true, err);
    }

    public static void assertNeB(int[] a, int b, CodeEnum err) {
        if (DaoPredict.aEqB(a, b)) throw new CodeException(true, err);
    }

    public static void assertGtB(int[] a, int b, CodeEnum err) {
        if (DaoPredict.aLeB(a, b)) throw new CodeException(true, err);
    }

    public static void assertGeB(int[] a, int b, CodeEnum err) {
        if (DaoPredict.aLtB(a, b)) throw new CodeException(true, err);
    }

    public static void assertLtB(int[] a, int b, CodeEnum err) {
        if (DaoPredict.aGeB(a, b)) throw new CodeException(true, err);
    }

    public static void assertLeB(int[] a, int b, CodeEnum err) {
        if (DaoPredict.aGtB(a, b)) throw new CodeException(true, err);
    }

    //

    public static int affectEq1(int a, CodeEnum err) {
        if (DaoPredict.aNeB(a, 1)) throw new CodeException(true, err);
        return a;
    }

    public static int affectLe1(int a, CodeEnum err) {
        if (DaoPredict.aGtB(a, 1)) throw new CodeException(true, err);
        return a;
    }

    public static int affectGe1(int a, CodeEnum err) {
        if (DaoPredict.aLtB(a, 1)) throw new CodeException(true, err);
        return a;
    }
    public static int[] affectEq1(int[] a, CodeEnum err) {
        if (DaoPredict.aNeB(a, 1)) throw new CodeException(true, err);
        return a;
    }

    public static int[] affectLe1(int[] a, CodeEnum err) {
        if (DaoPredict.aGtB(a, 1)) throw new CodeException(true, err);
        return a;
    }

    public static int[] affectGe1(int[] a, CodeEnum err) {
        if (DaoPredict.aLtB(a, 1)) throw new CodeException(true, err);
        return a;
    }
    //
    public static int affectEqB(int a, int b, CodeEnum err) {
        if (DaoPredict.aNeB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int affectNeB(int a, int b, CodeEnum err) {
        if (DaoPredict.aEqB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int affectGtB(int a, int b, CodeEnum err) {
        if (DaoPredict.aLeB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int affectGeB(int a, int b, CodeEnum err) {
        if (DaoPredict.aLtB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int affectLtB(int a, int b, CodeEnum err) {
        if (DaoPredict.aGeB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int affectLeB(int a, int b, CodeEnum err) {
        if (DaoPredict.aGtB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int[] affectEqB(int[] a, int b, CodeEnum err) {
        if (DaoPredict.aNeB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int[] affectNeB(int[] a, int b, CodeEnum err) {
        if (DaoPredict.aEqB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int[] affectGtB(int[] a, int b, CodeEnum err) {
        if (DaoPredict.aLeB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int[] affectGeB(int[] a, int b, CodeEnum err) {
        if (DaoPredict.aLtB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int[] affectLtB(int[] a, int b, CodeEnum err) {
        if (DaoPredict.aGeB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static int[] affectLeB(int[] a, int b, CodeEnum err) {
        if (DaoPredict.aGtB(a, b)) throw new CodeException(true, err);
        return a;
    }

    public static void assertEq1(int a, String err) {
        if (DaoPredict.aNeB(a, 1)) throw new IllegalStateException(err);
    }

    public static void assertLe1(int a, String err) {
        if (DaoPredict.aGtB(a, 1)) throw new IllegalStateException(err);
    }

    public static void assertGe1(int a, String err) {
        if (DaoPredict.aLtB(a, 1)) throw new IllegalStateException(err);
    }
    public static void assertEq1(int[] a, String err) {
        if (DaoPredict.aNeB(a, 1)) throw new IllegalStateException(err);
    }

    public static void assertLe1(int[] a, String err) {
        if (DaoPredict.aGtB(a, 1)) throw new IllegalStateException(err);
    }

    public static void assertGe1(int[] a, String err) {
        if (DaoPredict.aLtB(a, 1)) throw new IllegalStateException(err);
    }
    //
    public static void assertEqB(int a, int b, String err) {
        if (DaoPredict.aNeB(a, b)) throw new IllegalStateException(err);
    }

    public static void assertNeB(int a, int b, String err) {
        if (DaoPredict.aEqB(a, b)) throw new IllegalStateException(err);
    }

    public static void assertGtB(int a, int b, String err) {
        if (DaoPredict.aLeB(a, b)) throw new IllegalStateException(err);
    }

    public static void assertGeB(int a, int b, String err) {
        if (DaoPredict.aLtB(a, b)) throw new IllegalStateException(err);
    }

    public static void assertLtB(int a, int b, String err) {
        if (DaoPredict.aGeB(a, b)) throw new IllegalStateException(err);
    }

    public static void assertLeB(int a, int b, String err) {
        if (DaoPredict.aGtB(a, b)) throw new IllegalStateException(err);
    }

    public static void assertEqB(int[] a, int b, String err) {
        if (DaoPredict.aNeB(a, b)) throw new IllegalStateException(err);
    }

    public static void assertNeB(int[] a, int b, String err) {
        if (DaoPredict.aEqB(a, b)) throw new IllegalStateException(err);
    }

    public static void assertGtB(int[] a, int b, String err) {
        if (DaoPredict.aLeB(a, b)) throw new IllegalStateException(err);
    }

    public static void assertGeB(int[] a, int b, String err) {
        if (DaoPredict.aLtB(a, b)) throw new IllegalStateException(err);
    }

    public static void assertLtB(int[] a, int b, String err) {
        if (DaoPredict.aGeB(a, b)) throw new IllegalStateException(err);
    }

    public static void assertLeB(int[] a, int b, String err) {
        if (DaoPredict.aGtB(a, b)) throw new IllegalStateException(err);
    }

    //

    public static int affectEq1(int a, String err) {
        if (DaoPredict.aNeB(a, 1)) throw new IllegalStateException(err);
        return a;
    }

    public static int affectLe1(int a, String err) {
        if (DaoPredict.aGtB(a, 1)) throw new IllegalStateException(err);
        return a;
    }

    public static int affectGe1(int a, String err) {
        if (DaoPredict.aLtB(a, 1)) throw new IllegalStateException(err);
        return a;
    }
    public static int[] affectEq1(int[] a, String err) {
        if (DaoPredict.aNeB(a, 1)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] affectLe1(int[] a, String err) {
        if (DaoPredict.aGtB(a, 1)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] affectGe1(int[] a, String err) {
        if (DaoPredict.aLtB(a, 1)) throw new IllegalStateException(err);
        return a;
    }
    //
    public static int affectEqB(int a, int b, String err) {
        if (DaoPredict.aNeB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int affectNeB(int a, int b, String err) {
        if (DaoPredict.aEqB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int affectGtB(int a, int b, String err) {
        if (DaoPredict.aLeB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int affectGeB(int a, int b, String err) {
        if (DaoPredict.aLtB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int affectLtB(int a, int b, String err) {
        if (DaoPredict.aGeB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int affectLeB(int a, int b, String err) {
        if (DaoPredict.aGtB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] affectEqB(int[] a, int b, String err) {
        if (DaoPredict.aNeB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] affectNeB(int[] a, int b, String err) {
        if (DaoPredict.aEqB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] affectGtB(int[] a, int b, String err) {
        if (DaoPredict.aLeB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] affectGeB(int[] a, int b, String err) {
        if (DaoPredict.aLtB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] affectLtB(int[] a, int b, String err) {
        if (DaoPredict.aGeB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static int[] affectLeB(int[] a, int b, String err) {
        if (DaoPredict.aGtB(a, b)) throw new IllegalStateException(err);
        return a;
    }

    public static void assertEq1(int a, String err, Object... arg) {
        if (DaoPredict.aNeB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertLe1(int a, String err, Object... arg) {
        if (DaoPredict.aGtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertGe1(int a, String err, Object... arg) {
        if (DaoPredict.aLtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }
    public static void assertEq1(int[] a, String err, Object... arg) {
        if (DaoPredict.aNeB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertLe1(int[] a, String err, Object... arg) {
        if (DaoPredict.aGtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertGe1(int[] a, String err, Object... arg) {
        if (DaoPredict.aLtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }
    //
    public static void assertEqB(int a, int b, String err, Object... arg) {
        if (DaoPredict.aNeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertNeB(int a, int b, String err, Object... arg) {
        if (DaoPredict.aEqB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertGtB(int a, int b, String err, Object... arg) {
        if (DaoPredict.aLeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertGeB(int a, int b, String err, Object... arg) {
        if (DaoPredict.aLtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertLtB(int a, int b, String err, Object... arg) {
        if (DaoPredict.aGeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertLeB(int a, int b, String err, Object... arg) {
        if (DaoPredict.aGtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertEqB(int[] a, int b, String err, Object... arg) {
        if (DaoPredict.aNeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertNeB(int[] a, int b, String err, Object... arg) {
        if (DaoPredict.aEqB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertGtB(int[] a, int b, String err, Object... arg) {
        if (DaoPredict.aLeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertGeB(int[] a, int b, String err, Object... arg) {
        if (DaoPredict.aLtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertLtB(int[] a, int b, String err, Object... arg) {
        if (DaoPredict.aGeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    public static void assertLeB(int[] a, int b, String err, Object... arg) {
        if (DaoPredict.aGtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
    }

    //

    public static int affectEq1(int a, String err, Object... arg) {
        if (DaoPredict.aNeB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int affectLe1(int a, String err, Object... arg) {
        if (DaoPredict.aGtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int affectGe1(int a, String err, Object... arg) {
        if (DaoPredict.aLtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }
    public static int[] affectEq1(int[] a, String err, Object... arg) {
        if (DaoPredict.aNeB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] affectLe1(int[] a, String err, Object... arg) {
        if (DaoPredict.aGtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] affectGe1(int[] a, String err, Object... arg) {
        if (DaoPredict.aLtB(a, 1)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }
    //
    public static int affectEqB(int a, int b, String err, Object... arg) {
        if (DaoPredict.aNeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int affectNeB(int a, int b, String err, Object... arg) {
        if (DaoPredict.aEqB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int affectGtB(int a, int b, String err, Object... arg) {
        if (DaoPredict.aLeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int affectGeB(int a, int b, String err, Object... arg) {
        if (DaoPredict.aLtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int affectLtB(int a, int b, String err, Object... arg) {
        if (DaoPredict.aGeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int affectLeB(int a, int b, String err, Object... arg) {
        if (DaoPredict.aGtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] affectEqB(int[] a, int b, String err, Object... arg) {
        if (DaoPredict.aNeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] affectNeB(int[] a, int b, String err, Object... arg) {
        if (DaoPredict.aEqB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] affectGtB(int[] a, int b, String err, Object... arg) {
        if (DaoPredict.aLeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] affectGeB(int[] a, int b, String err, Object... arg) {
        if (DaoPredict.aLtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] affectLtB(int[] a, int b, String err, Object... arg) {
        if (DaoPredict.aGeB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }

    public static int[] affectLeB(int[] a, int b, String err, Object... arg) {
        if (DaoPredict.aGtB(a, b)) throw new IllegalStateException(FormatUtil.logback(err, arg));
        return a;
    }
}

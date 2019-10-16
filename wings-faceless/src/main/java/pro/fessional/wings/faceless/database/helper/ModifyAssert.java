package pro.fessional.wings.faceless.database.helper;

import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.pain.CodeException;

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
        if (isANeB(a, 1)) throw new CodeException(err);
        return true;
    }

    public static boolean less(int a, CodeEnum err) {
        if (isAGtB(a, 1)) throw new CodeException(err);
        return true;
    }

    public static boolean more(int a, CodeEnum err) {
        if (isALtB(a, 1)) throw new CodeException(err);
        return true;
    }
    public static boolean one(int[] a, CodeEnum err) {
        if (isANeB(a, 1)) throw new CodeException(err);
        return true;
    }

    public static boolean less(int[] a, CodeEnum err) {
        if (isAGtB(a, 1)) throw new CodeException(err);
        return true;
    }

    public static boolean more(int[] a, CodeEnum err) {
        if (isALtB(a, 1)) throw new CodeException(err);
        return true;
    }
    //
    public static boolean aEqB(int a, int b, CodeEnum err) {
        if (isANeB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aNeB(int a, int b, CodeEnum err) {
        if (isAEqB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aGtB(int a, int b, CodeEnum err) {
        if (isALeB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aGeB(int a, int b, CodeEnum err) {
        if (isALtB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aLtB(int a, int b, CodeEnum err) {
        if (isAGeB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aLeB(int a, int b, CodeEnum err) {
        if (isAGtB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aEqB(int[] a, int b, CodeEnum err) {
        if (isANeB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aNeB(int[] a, int b, CodeEnum err) {
        if (isAEqB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aGtB(int[] a, int b, CodeEnum err) {
        if (isALeB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aGeB(int[] a, int b, CodeEnum err) {
        if (isALtB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aLtB(int[] a, int b, CodeEnum err) {
        if (isAGeB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aLeB(int[] a, int b, CodeEnum err) {
        if (isAGtB(a, b)) throw new CodeException(err);
        return true;
    }

    //

    public static int one$(int a, CodeEnum err) {
        if (isANeB(a, 1)) throw new CodeException(err);
        return a;
    }

    public static int less$(int a, CodeEnum err) {
        if (isAGtB(a, 1)) throw new CodeException(err);
        return a;
    }

    public static int more$(int a, CodeEnum err) {
        if (isALtB(a, 1)) throw new CodeException(err);
        return a;
    }
    public static int[] one$(int[] a, CodeEnum err) {
        if (isANeB(a, 1)) throw new CodeException(err);
        return a;
    }

    public static int[] less$(int[] a, CodeEnum err) {
        if (isAGtB(a, 1)) throw new CodeException(err);
        return a;
    }

    public static int[] more$(int[] a, CodeEnum err) {
        if (isALtB(a, 1)) throw new CodeException(err);
        return a;
    }
    //
    public static int aEqB$(int a, int b, CodeEnum err) {
        if (isANeB(a, b)) throw new CodeException(err);
        return a;
    }

    public static int aNeB$(int a, int b, CodeEnum err) {
        if (isAEqB(a, b)) throw new CodeException(err);
        return a;
    }

    public static int aGtB$(int a, int b, CodeEnum err) {
        if (isALeB(a, b)) throw new CodeException(err);
        return a;
    }

    public static int aGeB$(int a, int b, CodeEnum err) {
        if (isALtB(a, b)) throw new CodeException(err);
        return a;
    }

    public static int aLtB$(int a, int b, CodeEnum err) {
        if (isAGeB(a, b)) throw new CodeException(err);
        return a;
    }

    public static int aLeB$(int a, int b, CodeEnum err) {
        if (isAGtB(a, b)) throw new CodeException(err);
        return a;
    }

    public static int[] aEqB$(int[] a, int b, CodeEnum err) {
        if (isANeB(a, b)) throw new CodeException(err);
        return a;
    }

    public static int[] aNeB$(int[] a, int b, CodeEnum err) {
        if (isAEqB(a, b)) throw new CodeException(err);
        return a;
    }

    public static int[] aGtB$(int[] a, int b, CodeEnum err) {
        if (isALeB(a, b)) throw new CodeException(err);
        return a;
    }

    public static int[] aGeB$(int[] a, int b, CodeEnum err) {
        if (isALtB(a, b)) throw new CodeException(err);
        return a;
    }

    public static int[] aLtB$(int[] a, int b, CodeEnum err) {
        if (isAGeB(a, b)) throw new CodeException(err);
        return a;
    }

    public static int[] aLeB$(int[] a, int b, CodeEnum err) {
        if (isAGtB(a, b)) throw new CodeException(err);
        return a;
    }
}

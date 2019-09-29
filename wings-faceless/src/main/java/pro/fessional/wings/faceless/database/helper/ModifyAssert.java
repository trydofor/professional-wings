package pro.fessional.wings.faceless.database.helper;

import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.pain.CodeException;

/**
 * @author trydofor
 * @since 2019-09-18
 */
public class ModifyAssert {

    public static boolean zero(int a) {
        return aEqB(a, 0);
    }

    public static boolean one(int a) {
        return aEqB(a, 1);
    }

    public static boolean more(int a) {
        return aGeB(a, 2);
    }

    public static boolean aEqB(int a, int b) {
        return a == b;
    }

    public static boolean aGtB(int a, int b) {
        return a > b;
    }

    public static boolean aGeB(int a, int b) {
        return a >= b;
    }

    public static boolean zero(int[] a) {
        return aEqB(a, 0);
    }

    public static boolean one(int[] a) {
        return aEqB(a, 1);
    }

    public static boolean more(int[] a) {
        return aGeB(a, 2);
    }

    public static boolean aEqB(int[] a, int b) {
        if (a == null || a.length == 0) return false;
        for (int i : a) {
            if (i != b) return false;
        }
        return true;
    }

    public static boolean aGtB(int[] a, int b) {
        if (a == null || a.length == 0) return false;
        for (int i : a) {
            if (i <= b) return false;
        }
        return true;
    }

    public static boolean aGeB(int[] a, int b) {
        if (a == null || a.length == 0) return false;
        for (int i : a) {
            if (i < b) return false;
        }
        return true;
    }

    // ////

    public static boolean zeroOrThrow(int a, CodeEnum err) {
        if (!zero(a)) throw new CodeException(err);
        return true;
    }

    public static boolean oneOrThrow(int a, CodeEnum err) {
        if (!one(a)) throw new CodeException(err);
        return true;
    }

    public static boolean moreOrThrow(int a, CodeEnum err) {
        if (!more(a)) throw new CodeException(err);
        return true;
    }

    public static boolean aEqBOrThrow(int a, int b, CodeEnum err) {
        if (!aEqB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aGtBOrThrow(int a, int b, CodeEnum err) {
        if (!aGtB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aGeBOrThrow(int a, int b, CodeEnum err) {
        if (!aGeB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean zeroOrThrow(int[] a, CodeEnum err) {
        if (!zero(a)) throw new CodeException(err);
        return true;
    }

    public static boolean oneOrThrow(int[] a, CodeEnum err) {
        if (!one(a)) throw new CodeException(err);
        return true;
    }

    public static boolean moreOrThrow(int[] a, CodeEnum err) {
        if (!more(a)) throw new CodeException(err);
        return true;
    }

    public static boolean aEqBOrThrow(int[] a, int b, CodeEnum err) {
        if (!aEqB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aGtBOrThrow(int[] a, int b, CodeEnum err) {
        if (!aGtB(a, b)) throw new CodeException(err);
        return true;
    }

    public static boolean aGeBOrThrow(int[] a, int b, CodeEnum err) {
        if (!aGeB(a, b)) throw new CodeException(err);
        return true;
    }
}

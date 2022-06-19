package pro.fessional.wings.faceless.database.helper;

/**
 * @author trydofor
 * @since 2019-09-18
 */
public class ModifyPredict {

    public static boolean isAEqB(int a, int b) {
        return a == b;
    }

    public static boolean isANeB(int a, int b) {
        return a != b;
    }

    public static boolean isAGtB(int a, int b) {
        return a > b;
    }

    public static boolean isAGeB(int a, int b) {
        return a >= b;
    }

    public static boolean isALtB(int a, int b) {
        return a < b;
    }

    public static boolean isALeB(int a, int b) {
        return a <= b;
    }

    public static boolean isAEqB(int[] a, int b) {
        if (a == null || a.length == 0) return false;
        for (int i : a) {
            if (i != b) return false;
        }
        return true;
    }

    public static boolean isANeB(int[] a, int b) {
        if (a == null || a.length == 0) return false;
        for (int i : a) {
            if (i == b) return false;
        }
        return true;
    }

    public static boolean isAGtB(int[] a, int b) {
        if (a == null || a.length == 0) return false;
        for (int i : a) {
            if (i <= b) return false;
        }
        return true;
    }

    public static boolean isAGeB(int[] a, int b) {
        if (a == null || a.length == 0) return false;
        for (int i : a) {
            if (i < b) return false;
        }
        return true;
    }

    public static boolean isALtB(int[] a, int b) {
        if (a == null || a.length == 0) return false;
        for (int i : a) {
            if (i >= b) return false;
        }
        return true;
    }

    public static boolean isALeB(int[] a, int b) {
        if (a == null || a.length == 0) return false;
        for (int i : a) {
            if (i > b) return false;
        }
        return true;
    }
}

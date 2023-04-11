package pro.fessional.wings.faceless.database.helper;

/**
 * @author trydofor
 * @since 2019-09-18
 */
public class DaoPredict {

    public static boolean aEqB(int a, int b) {
        return a == b;
    }

    public static boolean aNeB(int a, int b) {
        return a != b;
    }

    public static boolean aGtB(int a, int b) {
        return a > b;
    }

    public static boolean aGeB(int a, int b) {
        return a >= b;
    }

    public static boolean aLtB(int a, int b) {
        return a < b;
    }

    public static boolean aLeB(int a, int b) {
        return a <= b;
    }

    public static boolean aEqB(int[] a, int b) {
        if (a == null || a.length == 0) return false;
        for (int i : a) {
            if (i != b) return false;
        }
        return true;
    }

    public static boolean aNeB(int[] a, int b) {
        if (a == null || a.length == 0) return false;
        for (int i : a) {
            if (i == b) return false;
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

    public static boolean aLtB(int[] a, int b) {
        if (a == null || a.length == 0) return false;
        for (int i : a) {
            if (i >= b) return false;
        }
        return true;
    }

    public static boolean aLeB(int[] a, int b) {
        if (a == null || a.length == 0) return false;
        for (int i : a) {
            if (i > b) return false;
        }
        return true;
    }
}

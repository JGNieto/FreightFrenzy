package org.baylorschool.library.math;

public class MinPower {
    public static double minPower(double power, double minimum) {
        int sign = power < 0 ? -1 : 1;
        return sign * Math.max(Math.abs(power), Math.abs(minimum));
    }
}

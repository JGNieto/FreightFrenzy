package org.baylorschool.library.localization;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.library.Location;

public class ColorSensors {
    private ColorSensor leftSensor;
    private ColorSensor rightSensor;

    private boolean isLooking = false;

    private int[] leftBaseline = new int[] {50, 50, 50};
    private int[] rightBaseline = new int[] {50, 50, 50};

    public ColorSensors(HardwareMap hardwareMap) {
        try {
            this.leftSensor = hardwareMap.get(ColorSensor.class, Globals.leftColorSensor);
            this.rightSensor = hardwareMap.get(ColorSensor.class, Globals.rightColorSensor);
        } catch (Exception e) {
            leftSensor = null;
            rightSensor = null;
            e.printStackTrace();
        }
    }

    public ColorSensors(ColorSensor leftSensor, ColorSensor rightSensor) {
        this.leftSensor = leftSensor;
        this.rightSensor = rightSensor;
    }

    /**
     * Resets the baseline to compare future measurements.
     */
    public void resetBaseLine() {
        leftBaseline = new int[] {leftSensor.red(), leftSensor.green(), leftSensor.blue()};
        rightBaseline = new int[] {rightSensor.red(), rightSensor.green(), rightSensor.blue()};
    }

    private static boolean isWhite(ColorSensor colorSensor, int[] baseLine) {
        if (colorSensor == null) return false;
        double[] reading = new double[] {colorSensor.red(), colorSensor.green(), colorSensor.blue()};
        for (int i = 0; i < 3; ++i) {
            if (reading[i] < baseLine[i] * 1.7) return false;
        }
        return true;
    }

    public boolean isTrigger() {
        return isWhite(leftSensor, leftBaseline) && isWhite(rightSensor, rightBaseline);
    }

    public void computeLocation(Location location) {
        if (!isLooking) return;
        if (!isTrigger()) return;
        if (Math.abs(location.getHeading()) > 10) return;

        isLooking = false;
        location.setX(Places.awayParallel(1) + 150);
        System.out.println("Updated location based on color sensor.");
    }

    public ColorSensor getLeftSensor() {
        return leftSensor;
    }

    public void setLeftSensor(ColorSensor leftSensor) {
        this.leftSensor = leftSensor;
    }

    public ColorSensor getRightSensor() {
        return rightSensor;
    }

    public void setRightSensor(ColorSensor rightSensor) {
        this.rightSensor = rightSensor;
    }

    public boolean isLooking() {
        return isLooking;
    }

    public void setLooking(boolean looking) {
        this.isLooking = looking;
    }
}

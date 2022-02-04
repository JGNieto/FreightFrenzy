package org.baylorschool.library.localization;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.library.Location;

public class TouchSensors {
    private TouchSensor touchSensorLeft;
    private TouchSensor touchSensorRight;
    private TouchSensor touchSensorBack;

    public TouchSensors(HardwareMap hardwareMap) {
        try {
            touchSensorLeft = hardwareMap.get(TouchSensor.class, Globals.touchLeft);
        } catch (Exception e) {
            // We can live without left sensor.
        }
        try {
            touchSensorRight = hardwareMap.get(TouchSensor.class, Globals.touchRight);
        } catch (Exception e) {
            // We can live without right sensor.
        }
        try {
            touchSensorBack = hardwareMap.get(TouchSensor.class, Globals.touchBack);
        } catch (Exception e) {
            // We can live without back sensor.
        }
    }

    public enum Direction {
        LEFT, RIGHT, BACK;
    }

    /**
     * Uses a push button or another mechanism to detect whether the robot is touching a wall, or something
     * on its left side.
     * NOTE: IF THERE IS NO HARDWARE INSTALLED TO MAKE THE DETECTION, THIS METHOD WILL RETURN
     * FALSE BY DEFAULT, AND WILL NOT THROW AN EXCEPTION OR ANY OTHER INDICATION.
     * You may want to use the method canDetectLeft().
     * @return Whether there is a press detected on the left side of the robot.
     */
    public boolean leftPressed() {
        return touchSensorLeft != null && touchSensorLeft.isPressed();
    }

    /**
     * Since the method leftPressed() returns false by default when no hardware is installed, this method
     * indicates whether there is hardware available, independently of the value of leftPressed().
     * @return Whether there is hardware installed to detect with leftPressed().
     */
    public boolean canDetectLeft() {
        return touchSensorLeft != null;
    }


    /**
     * Uses a push button or another mechanism to detect whether the robot is touching a wall, or something
     * on its right side.
     * NOTE: IF THERE IS NO HARDWARE INSTALLED TO MAKE THE DETECTION, THIS METHOD WILL RETURN
     * FALSE BY DEFAULT, AND WILL NOT THROW AN EXCEPTION OR ANY OTHER INDICATION.
     * You may want to use the method canDetectRight().
     * @return Whether there is a press detected on the right side of the robot.
     */
    public boolean rightPressed() {
        return touchSensorRight != null && touchSensorRight.isPressed();
    }

    /**
     * Since the method rightPressed() returns false by default when no hardware is installed, this method
     * indicates whether there is hardware available, independently of the value of rightPressed().
     * @return Whether there is hardware installed to detect with rightPressed().
     */
    public boolean canDetectRight() {
        return touchSensorRight != null;
    }


    /**
     * Uses a push button or another mechanism to detect whether the robot is touching a wall, or something
     * on its back side.
     * NOTE: IF THERE IS NO HARDWARE INSTALLED TO MAKE THE DETECTION, THIS METHOD WILL RETURN
     * FALSE BY DEFAULT, AND WILL NOT THROW AN EXCEPTION OR ANY OTHER INDICATION.
     * You may want to use the method canDetectBack().
     * @return Whether there is a press detected on the back side of the robot.
     */
    public boolean backPressed() {
        return touchSensorBack != null && touchSensorBack.isPressed();
    }

    /**
     * Since the method backPressed() returns false by default when no hardware is installed, this method
     * indicates whether there is hardware available, independently of the value of backPressed().
     * @return Whether there is hardware installed to detect with backPressed().
     */
    public boolean canDetectBack() {
        return touchSensorBack != null;
    }


    /**
     * Uses a push button or another mechanism to detect whether the robot is touching a wall, or something
     * on the indicated side.
     * NOTE: IF THERE IS NO HARDWARE INSTALLED TO MAKE THE DETECTION, THIS METHOD WILL RETURN
     * FALSE BY DEFAULT, AND WILL NOT THROW AN EXCEPTION OR ANY OTHER INDICATION.
     * You may want to use the method canDetect().
     * @param direction Direction to detect.
     * @return Whether there is a press detected on the indicated side of the robot.
     */
    public boolean pressed(Direction direction) {
        switch (direction) {
            case BACK:
                return backPressed();
            case LEFT:
                return leftPressed();
            case RIGHT:
                return rightPressed();
            default:
                return false;
        }
    }

    /**
     * Since the method pressed() returns false by default when no hardware is installed, this method
     * indicates whether there is hardware available, independently of the value of pressed().
     * @param direction Direction to detect.
     * @return Whether there is hardware installed to detect with pressed().
     */
    public boolean canDetect(Direction direction) {
        switch (direction) {
            case BACK:
                return canDetectBack();
            case LEFT:
                return canDetectLeft();
            case RIGHT:
                return canDetectRight();
            default:
                return false;
        }
    }

    /**
     * Computes special cases of localization.
     * @param currentLocation Current location. IT IS MUTATED.
     */
    public void computeLocation(Location currentLocation) {
        // Check switches
        if (leftPressed() || rightPressed()) {
            boolean left = leftPressed();
            boolean right = rightPressed();

            double angle = currentLocation.getHeading();
            double absAngle = Math.abs(angle);

            // This program assumes the IMU is mostly right, and that it will not go crazy. Thus,
            // we use that IMU heading to determine which wall we have hit.
            if (absAngle >= 175) {
                if (left) {
                    currentLocation.setY(Places.closePerpendicular(-3));
                } else if (right) {
                    currentLocation.setY(Places.closePerpendicular(+3));
                }
            } else if (absAngle <= 5) {
                if (left) {
                    currentLocation.setY(Places.closePerpendicular(+3));
                } else if (right) {
                    currentLocation.setY(Places.closePerpendicular(-3));
                }
            } else if (85 <= absAngle && absAngle <= 95) {
                int angleSign = angle < 0 ? -1 : 1;
                if (left) {
                    currentLocation.setX(Places.closePerpendicular(-3) * angleSign);
                } else if (right) {
                    currentLocation.setX(Places.closePerpendicular(+3) * angleSign);
                }
            }
        }
    }
}

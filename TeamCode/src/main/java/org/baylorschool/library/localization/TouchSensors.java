package org.baylorschool.library.localization;

public interface TouchSensors {
    enum Direction {
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
    boolean leftPressed();

    /**
     * Since the method leftPressed() returns false by default when no hardware is installed, this method
     * indicates whether there is hardware available, independently of the value of leftPressed().
     * @return Whether there is hardware installed to detect with leftPressed().
     */
    boolean canDetectLeft();

    /**
     * Uses a push button or another mechanism to detect whether the robot is touching a wall, or something
     * on its right side.
     * NOTE: IF THERE IS NO HARDWARE INSTALLED TO MAKE THE DETECTION, THIS METHOD WILL RETURN
     * FALSE BY DEFAULT, AND WILL NOT THROW AN EXCEPTION OR ANY OTHER INDICATION.
     * You may want to use the method canDetectRight().
     * @return Whether there is a press detected on the right side of the robot.
     */
    boolean rightPressed();

    /**
     * Since the method rightPressed() returns false by default when no hardware is installed, this method
     * indicates whether there is hardware available, independently of the value of rightPressed().
     * @return Whether there is hardware installed to detect with rightPressed().
     */
    boolean canDetectRight();

    /**
     * Uses a push button or another mechanism to detect whether the robot is touching a wall, or something
     * on its back side.
     * NOTE: IF THERE IS NO HARDWARE INSTALLED TO MAKE THE DETECTION, THIS METHOD WILL RETURN
     * FALSE BY DEFAULT, AND WILL NOT THROW AN EXCEPTION OR ANY OTHER INDICATION.
     * You may want to use the method canDetectBack().
     * @return Whether there is a press detected on the back side of the robot.
     */
    boolean backPressed();

    /**
     * Since the method backPressed() returns false by default when no hardware is installed, this method
     * indicates whether there is hardware available, independently of the value of backPressed().
     * @return Whether there is hardware installed to detect with backPressed().
     */
    boolean canDetectBack();

    /**
     * Uses a push button or another mechanism to detect whether the robot is touching a wall, or something
     * on the indicated side.
     * NOTE: IF THERE IS NO HARDWARE INSTALLED TO MAKE THE DETECTION, THIS METHOD WILL RETURN
     * FALSE BY DEFAULT, AND WILL NOT THROW AN EXCEPTION OR ANY OTHER INDICATION.
     * You may want to use the method canDetect().
     * @param direction Direction to detect.
     * @return Whether there is a press detected on the indicated side of the robot.
     */
    default boolean pressed(Direction direction) {
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
    default boolean canDetect(Direction direction) {
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
}

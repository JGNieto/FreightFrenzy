package org.baylorschool;

import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.opencv.core.Scalar;

public class Globals {

    /**
     * READ ME: This class is here for IDE Intellisense purposes. The license should never be uploaded
     * to the GitHub repo, only on the bot with OnBotJava, or placed here temporarily when not using any
     * VCS, and deleted BEFORE committing. If the license is exposed to the GitHub by accident, warn
     * Javier to revoke it and create a new one. The actual key is in the OneNote for programming.
     */
    /**
     * TL;DR: Upload this class to the robot using OnBotJava and replace PLACEHOLDER with the key
     * found on the OneNote.
     */
    public static final String VUFORIA_LICENSE = "PLACEHOLDER";

    public enum DropLevel {
        BOTTOM,
        MIDDLE,
        TOP,
    }

    ///////////////////////////// HARDWARE CONFIG /////////////////////////////
    // Names of the config MUST match these values
    // Motors
    public static final String blMotorHw = "blMotor";
    public static final String brMotorHw = "brMotor";
    public static final String flMotorHw = "flMotor";
    public static final String frMotorHw = "frMotor";

    // Lift
    public static final String rollerHw = "roller";
    public static final String twoBarHw = "twobar";

    // Servos (Odometry)
    public static final String servoLeftHw = "servoLeft";
    public static final String servoRightHw = "servoRight";
    public static final String servoMiddleHw = "servoMiddle";

    ///////////////////////////// IMU /////////////////////////////
    // Axis (may change depending on the orientation of the Control Hub)
    public static final IMU.Axis imuAxis = IMU.Axis.X;

    ///////////////////////////// LOCATION /////////////////////////////
    // Default values of the location
    public static final double defaultPurePursuitRadius = 250; // TODO: Placeholder
    public static final double defaultPurePursuitTurnSpeed = 0; // TODO: Placeholder

    ///////////////////////////// MECANUM /////////////////////////////
    // Which side of the chassis' wheels are reverse.
    public static final Mecanum.Side reverseSide = Mecanum.Side.RIGHT;

    // Details about the wheels
    public static final double ticksPerRevolution = 537.7;
    public static final double wheelDiameter = 100; // In millimeters
    public static final double ticksPerMm = ticksPerRevolution / (Math.PI * wheelDiameter);

    // Master coefficient for autonomous
    public static final double autonomousSpeed = 0.3;

    // Encoder ticks to rotate 360 degrees.
    public static final double fullTurnEncoderCountFL = 2975;
    public static final double fullTurnEncoderCountFR = 3100;
    public static final double fullTurnEncoderCountBL = 3525;
    public static final double fullTurnEncoderCountBR = 3420;

    // In mm, the distance between two diagonally opposed wheels.
    // (also twice the distance of any wheel from the center of the robot)
    // TODO: In the future, we may need to use two diameters because chassis now
    // looks closer to ellipse than circle.
    public static final double turningDiameter = 540;

    ///////////////////////////// ODOMETRY /////////////////////////////
    // FIXME: GET THESE VALUES CORRECT
    public static final double odometryTicksPerRevolution = -1;
    public static final double wheelRadius = 30;
    public static final double dPar = 100; // Distance between center of robot and parallel wheels.
    public static final double dPer = -70; // Distance between center of robot and perpendicular wheel.

    // Servo position value for the respective positions.
    public static final double positionWithdrawnRight = 0;
    public static final double positionWithdrawnLeft = 0;
    public static final double positionWithdrawnMiddle = 0;
    public static final double positionOpenRight = 0.45;
    public static final double positionOpenLeft = 0.45;
    public static final double positionOpenMiddle = 0.45;

    public static final double mmPerTick = 2 * Math.PI * wheelRadius / ticksPerRevolution;

    ///////////////////////////// PATH /////////////////////////////
    // Default tolerance (for some movements, distance within which the robot must be of the target
    // to consider itself at the target).
    public static final Location defaultTolerance = new Location(50, 50, -1, -1, -1, 3);

    ///////////////////////////// TEAM SHIPPING ELEMENT VISION PIPELINE /////////////////////////////
    public static int screenHeight = 240;
    public static int screenWidth = 320;
    public static double rectHeightFraction = 0.4;
    public static int rectHeight = (int) (rectHeightFraction * screenHeight) - 1;
    public static int rectWidth = screenWidth / 3 - 1;

    public static Scalar greenDetectionLowerThreshold = new Scalar(36, 60, 60);
    public static Scalar greenDetectionUpperThreshold = new Scalar(86, 255, 255);

    ///////////////////////////// TWO BAR LIFT /////////////////////////////
    public static int releaseDelay = 2000; // Time (ms) during which the roller is active during release.

    ///////////////////////////// CAROUSEL SPEEDS /////////////////////////////
    // Speeds used during the autonomous three-part movement. First, slowSpeed is run, then medium
    // and, finally, fast.
    // Currently not in use.
    public static final double carouselSlowSpeed = 0.65;
    public static final double carouselMediumSpeed = 0.8;
    public static final double carouselFastSpeed = 1;

    // Single speed used with only one pause
    // Right now, only one speed is used, and the carousel is paused once.
    public static final double carouselSingleSpeed = 0.5;
    public static final int carouselSinglePause = 2500;



}

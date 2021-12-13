package org.baylorschool;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.lift.Lift;
import org.baylorschool.library.lift.TwoBarLift;
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
    // Motors (in parentheses the preferred port number on the control hub)
    // If using dead wheel odometry, the wheel that must be connected to each port is listed.
    public static final String flMotorHw = "flMotor"; // (0) - Odometry Encoder Left
    public static final String frMotorHw = "frMotor"; // (1) - Odometry Encoder Right
    public static final String blMotorHw = "blMotor"; // (2) - Odometry Encoder Middle
    public static final String brMotorHw = "brMotor"; // (3)

    // Lift
    public static final String rollerHw = "roller"; // (0) Expansion Hub
    public static final String twoBarHw = "twobar"; // (1) Expansion Hub
    public static final String rollerSwitch = "rollerSwitch"; // (Digital 0) Expansion Hub

    // Fly Wheel (for carousel).
    public static final String flyWheel = "flyWheel";

    // Servos (Odometry)
    public static final String servoLeftHw = "servoLeft"; // (0)
    public static final String servoRightHw = "servoRight"; // (1)
    public static final String servoMiddleHw = "servoMiddle"; // (2)

    public static final String webcamDeviceName = "Webcam 1"; // Generally don't need to change.

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
    public static final double odometryTicksPerRevolution = 1440; // FIXME THIS IS WRONG
    public static final double wheelRadius = 23.5;
    public static final double dPar = 150.5; // Distance between center of robot and parallel wheels.
    public static final double dPer = -188; // Distance between center of robot and perpendicular wheel.

    // Encoder readings for the odometry will be multiplied times the following coefficients.
    // They are used to change the sign of the value.
    public static final int leftOdometryCoefficient = -1;
    public static final int rightOdometryCoefficient = -1;
    public static final int middleOdometryCoefficient = 1;

    // Servo position value for the respective servos and positions.
    public static final double positionWithdrawnRight = 0;
    public static final double positionWithdrawnLeft = 1;
    public static final double positionWithdrawnMiddle = 0;
    public static final double positionOpenRight = 0.5;
    public static final double positionOpenLeft = 0.3;
    public static final double positionOpenMiddle = 0.3;

    public static final double mmPerTick = 2 * Math.PI * wheelRadius / odometryTicksPerRevolution;

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

    ///////////////////////////// LIFT /////////////////////////////
    // The following method is called every time a lift is created.
    // If using, say, a cascade lift, change TwoBarLift to CascadeLift.
    public static Lift createNewLift(LinearOpMode opMode) {
        return new TwoBarLift(opMode);
    }

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

    ///////////////////////////// VUFORIA /////////////////////////////
    // Physical position of the camera.
    public static final float CAMERA_FORWARD_DISPLACEMENT = 192;
    public static final float CAMERA_LEFT_DISPLACEMENT = -114;
    public static final float CAMERA_VERTICAL_DISPLACEMENT = 124;

}

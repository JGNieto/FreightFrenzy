    package org.baylorschool;

    import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

    import org.baylorschool.library.IMU;
    import org.baylorschool.library.Location;
    import org.baylorschool.library.Mecanum;
    import org.baylorschool.library.ftclib.PIDFController;
    import org.baylorschool.library.lift.Lift;
    import org.baylorschool.library.lift.TwoBarLift;
    import org.opencv.core.Scalar;

    public class Globals {

        public static final String VUFORIA_LICENSE = "Aabb6tf/////AAABmSBibmY6dkQil4rqX+dxkG53PUPetQoaTQVeWGLB5Cxtk4A6GNd0SKK5jwv0FnTDZhZjf+eYQurmTMlGnwpYPBxfxK3KkXz1Z/COEs4VHhN6TJ/E/9TcOQ5kaG+mhkjfug+9qu+dqQknCEUDgx7GKyva9vCcpKs3BBTAsWMo5R+oo4fVkg7/vL2pbkUufMAVlGfzellijvJIZZJiwjGOnygNOjBSlG0TieG4I3P2kALOu2NqV7gp8GA6D2Mynb8t/a6pc1Dsgo1M0bVwRvmZCaINHEkDkZiSpceOsGngoyDRhsrvkQFiI5RcIY4RfygXXgHcbxQPi+syBt0UeWk1Gy3SCemDXlEj7b/tz9NnYs7U";

        public enum DropLevel {
            BOTTOM,
            COOP,
            MIDDLE,
            TOP,
        }

        public enum WarehouseSide {
            RED, BLUE, COOP
        }

        ///////////////////////////// ROBOT SIZE /////////////////////////////
        // MILLIMETERS
        public static final double robotLength = 433;
        public static final double robotWidth = 337;

        ///////////////////////////// HARDWARE CONFIG /////////////////////////////
        // Names of the config MUST match these values.
        // Refer to OneNote > Team Project to see which ports are what.
        public static final String flMotorHw = "flMotor";
        public static final String frMotorHw = "frMotor";
        public static final String blMotorHw = "blMotor";
        public static final String brMotorHw = "brMotor";

        // Lift
        public static final String rollerHw = "roller";
        public static final String twoBarHw = "twobar";
        public static final String rollerSwitch = "rollerSwitch";

        // Lift LEDs
        public static final String ledIntakeFull = "ledRed";
        public static final String ledIntakeEmpty = "ledBlue";

        // Fly Wheel (for carousel).
        public static final String flyWheel = "flyWheel";

        // Servos (Odometry)
        public static final String servoLeftHw = "servoLeft";
        public static final String servoRightHw = "servoRight";
        public static final String servoMiddleHw = "servoMiddle";

        // Odometry (encoders)
        public static final String odometryEncoderLeft = "roller";
        public static final String odometryEncoderRight = "exHubOne";
        public static final String odometryEncoderMiddle = "flyWheel";

        // Tape (capping)
        public static final String tapeExtend = "tapeExtend";
        public static final String tapeTilt = "tapeTilt";

        // Color sensors
        public static final String leftColorSensor = "leftColorSensor";
        public static final String rightColorSensor = "rightColorSensor";

        // Touch sensors (localization)
        // Set strings to null if not connected.
        public static final String touchLeft = "touchLeft";
        public static final String touchRight = "touchRight";
        public static final String touchBack = "touchBack"; // Hardware not installed right now, but we provide support for the future.

        public static final String webcamDeviceName = "Webcam 1"; // Generally don't need to change.

        ///////////////////////////// PID /////////////////////////////
        public static PIDFController rotationPIDFController() {
            return new PIDFController(0.8, 5, 0, 0);
        }

        public static final double rotationPIDFMinPower = 0.1;
        public static final double rotationMinPower = 0;
        public static final double rotationPIDFCoefficient = 1.0 / 60.0;

        public static PIDFController movementPIDFController() {
            return new PIDFController(1.7, .7, 0, 0);
        }

        public static final double movementFineAdjustmentPower = 0.1;

        public static final double movementPIDFMinPower = 0;
        public static final double movementPIDFCoefficient = 1.0 / 800.0;

        ///////////////////////////// IMU /////////////////////////////
        // Axis (may change depending on the orientation of the Control Hub)
        public static final IMU.Axis imuRotationAxis = IMU.Axis.X;
        public static final IMU.Axis imuPitchAxis = IMU.Axis.Y;

        ///////////////////////////// LOCATION /////////////////////////////
        // Default values of the location
        public static final double defaultPurePursuitRadius = 250; // TODO: Placeholder
        public static final double defaultPurePursuitTurnSpeed = 1; // TODO: Placeholder
        public static final double defaultPurePursuitDistanceStopTurning = 200;
        public static final double defaultPurePursuitAngle = 0; // By default, the robot tries to drive forward.

        ///////////////////////////// AUTONOMOUS DECISION MAKING /////////////////////////////
        public static final double matchLength = 30000; // The autonomous match lasts 30 seconds (30000 ms).
        public static final double minTimeExitWarehouse = 13000; // Minimum time left to exit the warehouse.

        ///////////////////////////// MECANUM /////////////////////////////
        // Which side of the chassis' wheels are reverse.
        public static final Mecanum.Side reverseSide = Mecanum.Side.RIGHT;

        // Details about the wheels
        public static final double ticksPerRevolution = 537.7;
        public static final double wheelDiameter = 100; // In millimeters
        public static final double ticksPerMm = ticksPerRevolution / (Math.PI * wheelDiameter);

        // Master coefficient for autonomous
        public static double autonomousSpeed = 1;

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
        public static final double odometryTicksPerRevolution = 1440; // Javier originally measured this roughly and, surprisingly, correctly (we now know this is the exact number now because the part number is E8T-360 etc. and 360 * 4 = 1440).
        public static final double wheelRadius = 37.3 / 2;
        public static final double dPar = 305.0; // Distance between the parallel wheels.
        public static final double dPer = -196.0; // Distance between center of robot and perpendicular wheel. Negative because it is at the back.

        // Encoder readings for the odometry will be multiplied times the following coefficients.
        // They are used to change the sign of the value.
        // NOTE: The odometry code already takes into account whether the motor is FORWARD or REVERSE
        public static final int leftOdometryCoefficient = -1;
        public static final int rightOdometryCoefficient = 1;
        public static final int middleOdometryCoefficient = -1;

        // Servo position value for the respective servos and positions.
        public static final double positionWithdrawnRight = 0;
        public static final double positionWithdrawnLeft = 1;
        public static final double positionWithdrawnMiddle = 0;
        public static final double positionOpenRight = 0.64;
        public static final double positionOpenLeft = 0.565;
        public static final double positionOpenMiddle = 0.48;

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
        public static final double carouselSingleSpeed = 0.7;
        public static final int carouselSinglePause = 1150;

        ///////////////////////////// VUFORIA /////////////////////////////
        // Physical position of the camera.
        public static final float CAMERA_FORWARD_DISPLACEMENT = 192;
        public static final float CAMERA_LEFT_DISPLACEMENT = -114;
        public static final float CAMERA_VERTICAL_DISPLACEMENT = 124;

    }

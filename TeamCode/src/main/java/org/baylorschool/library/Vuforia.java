package org.baylorschool.library;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XZY;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.baylorschool.Globals;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

public class Vuforia {
    private HardwareMap hardwareMap;

    private static final float mmPerInch = 25.4f;
    private static final float mmTargetHeight = 6 * mmPerInch; // the height of the center of the target image above the floor
    private static final float halfField = 72 * mmPerInch;
    private static final float halfTile = 12 * mmPerInch;
    private static final float oneAndHalfTile = 36 * mmPerInch;

    private static final String webcamDeviceName = "Webcam 1";

    // Physical webcam location
    private static final float CAMERA_FORWARD_DISPLACEMENT = 192;
    private static final float CAMERA_LEFT_DISPLACEMENT = -114;
    private static final float CAMERA_VERTICAL_DISPLACEMENT = 124;

    private List<VuforiaTrackable> allTrackables = new ArrayList<>();
    private OpenGLMatrix cameraLocationOnRobot = null;

    private OpenGLMatrix lastMatrixLocation = null;
    private VuforiaLocalizer localizer = null;
    private VuforiaTrackables targets = null;
    private WebcamName webcamName = null;

    private boolean targetVisible = false;

    public Vuforia(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
    }

    public Location lookForTargets(Telemetry telemetry) {
        targetVisible = false;
        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                telemetry.addData("Visible Target", trackable.getName());
                targetVisible = true;

                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastMatrixLocation = robotLocationTransform;
                }
                break;
            }
        }

        if (targetVisible) {
            Location location = new Location(lastMatrixLocation);
            location.reportTelemtry(telemetry);
            return location;
        } else {
            telemetry.addData("Visible Target", "none");
            return null;
        }
    }

    public void startTracking() {
        targets.activate();
    }

    public void stopTracking() {
        targets.deactivate();
    }

    public void initializeParamers(boolean streamFeed) {
        webcamName = hardwareMap.get(WebcamName.class, webcamDeviceName);
        VuforiaLocalizer.Parameters parameters;
        if (streamFeed) {
            int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                    "cameraMonitorViewId",
                    "id",
                    hardwareMap.appContext.getPackageName()
            );
            parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        } else {
            parameters = new VuforiaLocalizer.Parameters();
        }

        parameters.vuforiaLicenseKey = Globals.VUFORIA_LICENSE;
        parameters.cameraName = webcamName;

        parameters.useExtendedTracking = true; // Might break stuff if true

        localizer = ClassFactory.getInstance().createVuforia(parameters);
        targets = this.localizer.loadTrackablesFromAsset("FreightFrenzy");
        allTrackables.addAll(targets);

        identifyTargets();

        cameraLocationOnRobot = OpenGLMatrix
                .translation(
                        CAMERA_FORWARD_DISPLACEMENT,
                        CAMERA_LEFT_DISPLACEMENT,
                        CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(
                        EXTRINSIC,
                        XZY,
                        DEGREES,
                        90, 90, 0
                ));

        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener())
                    .setCameraLocationOnRobot(parameters.cameraName, cameraLocationOnRobot);
        }
    }

    // ONLY VALID FOR FreightFrenzy
    private void identifyTargets() {
        identifyTarget(0, "Blue Storage",       -halfField,  oneAndHalfTile, mmTargetHeight, 90, 0, 90);
        identifyTarget(1, "Blue Alliance Wall",  halfTile,   halfField,      mmTargetHeight, 90, 0, 0);
        identifyTarget(2, "Red Storage",        -halfField, -oneAndHalfTile, mmTargetHeight, 90, 0, 90);
        identifyTarget(3, "Red Alliance Wall",   halfTile,  -halfField,      mmTargetHeight, 90, 0, 180);
    }

    /**
     * Identify a target by naming it, and setting its position and orientation on the field
     * @param targetIndex
     * @param targetName
     * @param dx, dy, dz  Target offsets in x,y,z axes
     * @param rx, ry, rz  Target rotations in x,y,z axes
     */
    private void identifyTarget(int targetIndex, String targetName, float dx, float dy, float dz, float rx, float ry, float rz) {
        VuforiaTrackable aTarget = targets.get(targetIndex);
        aTarget.setName(targetName);
        aTarget.setLocation(OpenGLMatrix.translation(dx, dy, dz)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, rx, ry, rz)));
    }
}

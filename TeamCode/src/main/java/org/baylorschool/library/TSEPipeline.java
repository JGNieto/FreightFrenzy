package org.baylorschool.library;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

public class TSEPipeline extends OpenCvPipeline {
    private Globals.DropLevel dropLevel = Globals.DropLevel.TOP;
    private Places.StartLocation startLocation;
    private Mat onlyGreen;
    private Mat mask;

    public static int screenHeight = 240;
    public static int screenWidth = 320;

    public static double rectHeightFraction = 0.4;
    public static int rectHeight = (int) (rectHeightFraction * screenHeight) - 1;

    public static int rectWidth = screenWidth / 3 - 1;

    private Rect topRect;
    private Rect middleRect;
    private Rect bottomRect;

    private Mat topMat;
    private Mat middleMat;
    private Mat bottomMat;

    private int topAvg;
    private int middleAvg;
    private int bottomAvg;

    public static OpenCvWebcam openWebcam(LinearOpMode opMode, OpenCvPipeline pipeline) {
        int cameraMonitorViewId = opMode.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", opMode.hardwareMap.appContext.getPackageName());
        OpenCvWebcam webcam = OpenCvCameraFactory.getInstance().createWebcam(opMode.hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.setMillisecondsPermissionTimeout(2500);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.setPipeline(pipeline);
                webcam.startStreaming(TSEPipeline.screenWidth, TSEPipeline.screenHeight, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                opMode.telemetry.addData("Error opening the camera", errorCode);
                opMode.telemetry.update();
            }
        });

        return webcam;
    }

    public TSEPipeline(Places.StartLocation startLocation) {
        this.startLocation = startLocation;

        this.bottomRect = new Rect(0,screenHeight - rectHeight, rectWidth, rectHeight);
        this.middleRect = new Rect(screenWidth / 3,screenHeight - rectHeight, rectWidth, rectHeight);
        this.topRect = new Rect(2 * screenWidth / 3, screenHeight - rectHeight, rectWidth, rectHeight);
        /*if (startLocation == Places.StartLocation.BLUE_LEFT) {

        } else if (startLocation == Places.StartLocation.BLUE_RIGHT) {
            this.bottomRect = new Rect(0,screenHeight - rectHeight, rectWidth, rectHeight);
            this.middleRect = new Rect(screenWidth / 3,screenHeight - rectHeight, rectWidth, rectHeight);
            this.topRect = new Rect(2 * screenWidth / 3, screenHeight - rectHeight, rectWidth, rectHeight);
        } else if (startLocation == Places.StartLocation.RED_LEFT) {

        } else if (startLocation == Places.StartLocation.RED_RIGHT) {

        }*/
    }

    private void processChannel(Mat input) {
        Imgproc.cvtColor(input, onlyGreen, Imgproc.COLOR_RGB2HSV);
        Core.inRange(onlyGreen, new Scalar(36, 60, 60), new Scalar(86, 255, 255), onlyGreen);
    }

    // Dirty function to determine largest average of green.
    private void determineLargest() {
        if (topAvg >= middleAvg && middleAvg >= bottomAvg) {
            this.dropLevel = Globals.DropLevel.TOP;
        } else if (middleAvg >= topAvg && topAvg >= bottomAvg) {
            this.dropLevel = Globals.DropLevel.MIDDLE;
        } else {
            this.dropLevel = Globals.DropLevel.BOTTOM;
        }
    }

    @Override
    public void init(Mat firstFrame) {
        onlyGreen = new Mat();

        processChannel(firstFrame);

        this.topMat = onlyGreen.submat(topRect);
        this.middleMat = onlyGreen.submat(middleRect);
        this.bottomMat = onlyGreen.submat(bottomRect);
    }

    @Override
    public Mat processFrame(Mat input) {
        processChannel(input);

        this.topAvg = (int) Core.mean(topMat).val[0];
        this.middleAvg = (int) Core.mean(middleMat).val[0];
        this.bottomAvg = (int) Core.mean(bottomMat).val[0];

        determineLargest();

        Imgproc.rectangle(
                input,
                bottomRect,
                (dropLevel == Globals.DropLevel.BOTTOM ? new Scalar(0, 255, 0) : new Scalar(0, 0, 255)), 4);
        Imgproc.rectangle(
                input,
                middleRect,
                (dropLevel == Globals.DropLevel.MIDDLE ? new Scalar(0, 255, 0) : new Scalar(0, 0, 255)), 4);
        Imgproc.rectangle(
                input,
                topRect,
                (dropLevel == Globals.DropLevel.TOP ? new Scalar(0, 255, 0) : new Scalar(0, 0, 255)), 4);

        return onlyGreen;
    }

    public Globals.DropLevel getDropLevel() {
        return dropLevel;
    }

    public int getTopAvg() {
        return topAvg;
    }

    public int getMiddleAvg() {
        return middleAvg;
    }

    public int getBottomAvg() {
        return bottomAvg;
    }
}
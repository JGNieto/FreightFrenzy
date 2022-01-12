package org.baylorschool.library;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

public class TSEPipeline extends OpenCvPipeline {
    private volatile Globals.DropLevel dropLevel = Globals.DropLevel.TOP;
    private Mat onlyGreen;

    private LinearOpMode opMode;

    private Rect topRect;
    private Rect middleRect;
    private Rect bottomRect;

    private Mat topMat;
    private Mat middleMat;
    private Mat bottomMat;

    private volatile int topAvg;
    private volatile int middleAvg;
    private volatile int bottomAvg;

    public static OpenCvWebcam openWebcam(LinearOpMode opMode, OpenCvPipeline pipeline) {
        int cameraMonitorViewId = opMode.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", opMode.hardwareMap.appContext.getPackageName());
        OpenCvWebcam webcam = OpenCvCameraFactory.getInstance().createWebcam(opMode.hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.setMillisecondsPermissionTimeout(2500);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.setPipeline(pipeline);
                webcam.startStreaming(Globals.screenWidth, Globals.screenHeight, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                opMode.telemetry.addData("Error opening the camera", errorCode);
                opMode.telemetry.update();
            }
        });

        return webcam;
    }

    public TSEPipeline(LinearOpMode opMode) {
        this.opMode = opMode;

        this.bottomRect = new Rect(0,Globals.screenHeight - Globals.rectHeight, Globals.rectWidth, Globals.rectHeight);
        this.middleRect = new Rect(Globals.screenWidth / 3,Globals.screenHeight - Globals.rectHeight, Globals.rectWidth, Globals.rectHeight);
        this.topRect = new Rect(2 * Globals.screenWidth / 3, Globals.screenHeight - Globals.rectHeight, Globals.rectWidth, Globals.rectHeight);
    }

    private void processChannel(Mat input) {
        Imgproc.cvtColor(input, onlyGreen, Imgproc.COLOR_RGB2HSV);
        Core.inRange(onlyGreen, Globals.greenDetectionLowerThreshold, Globals.greenDetectionUpperThreshold, onlyGreen);
    }

    // Dirty function to determine where there is the largest amount of green.
    private void determineLargest() {
        if (topAvg >= middleAvg && topAvg >= bottomAvg) {
            this.dropLevel = Globals.DropLevel.TOP;
        } else if (middleAvg >= topAvg && middleAvg >= bottomAvg) {
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

        this.topMat = onlyGreen.submat(topRect);
        this.middleMat = onlyGreen.submat(middleRect);
        this.bottomMat = onlyGreen.submat(bottomRect);

        this.topAvg = (int) Core.sumElems(topMat).val[0];
        this.middleAvg = (int) Core.sumElems(middleMat).val[0];
        this.bottomAvg = (int) Core.sumElems(bottomMat).val[0];

        determineLargest();

        opMode.telemetry.addData("Drop Level", dropLevel.name());
        opMode.telemetry.addData("Top", topAvg);
        opMode.telemetry.addData("Middle", middleAvg);
        opMode.telemetry.addData("Bottom", bottomAvg);
        opMode.telemetry.update();

        return onlyGreen;
    }

    public static void stop(OpenCvWebcam webcam) {
        try {
            webcam.stopStreaming();
            webcam.closeCameraDevice();
        } catch (Exception e) {};
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
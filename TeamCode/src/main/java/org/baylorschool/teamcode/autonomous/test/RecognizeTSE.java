package org.baylorschool.teamcode.autonomous.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.library.TSEPipeline;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

// TSE = Team Shipping Element
@Autonomous(name="RecognizeTSE", group ="Test")
public class RecognizeTSE extends LinearOpMode {
    OpenCvWebcam webcam;
    TSEPipeline tsePipeline;

    @Override
    public void runOpMode() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        tsePipeline = new TSEPipeline(Places.StartLocation.BLUE_RIGHT);

        webcam.setMillisecondsPermissionTimeout(2500);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.setPipeline(tsePipeline);
                webcam.startStreaming(TSEPipeline.screenWidth, TSEPipeline.screenHeight, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Error opening the camera", errorCode);
                telemetry.update();
            }
        });

        telemetry.addLine("Waiting for start");
        telemetry.update();

        waitForStart();
        Globals.DropLevel dropLevel = tsePipeline.getDropLevel();
        //webcam.stopStreaming();
        //webcam.closeCameraDevice();

        while (opModeIsActive()) {
            telemetry.addData("Frame Count", webcam.getFrameCount());
            telemetry.addData("FPS", String.format("%.2f", webcam.getFps()));
            telemetry.addData("Total frame time ms", webcam.getTotalFrameTimeMs());
            telemetry.addData("Pipeline time ms", webcam.getPipelineTimeMs());
            telemetry.addData("Overhead time ms", webcam.getOverheadTimeMs());
            telemetry.addData("Theoretical max FPS", webcam.getCurrentPipelineMaxFps());
            telemetry.addData("Score Level", tsePipeline.getDropLevel().name());
            telemetry.addData("Bot Avg", tsePipeline.getBottomAvg());
            telemetry.addData("Mid Avg", tsePipeline.getMiddleAvg());
            telemetry.addData("Top Avg", tsePipeline.getTopAvg());
            telemetry.update();

            // FIXME: REMOVE SLEEP
            sleep(100);
        }
    }

}

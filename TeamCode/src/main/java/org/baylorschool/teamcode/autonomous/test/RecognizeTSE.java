package org.baylorschool.teamcode.autonomous.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.library.TSEPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

// TSE = Team Shipping Element
@Autonomous(name="RecognizeTSE", group ="Test")
public class RecognizeTSE extends LinearOpMode {
    OpenCvWebcam webcam;
    TSEPipeline tsePipeline;
    Globals.DropLevel dropLevel;

    @Override
    public void runOpMode() {
        tsePipeline = new TSEPipeline(this);
        webcam = TSEPipeline.openWebcam(this, tsePipeline);

        telemetry.addLine("Waiting for start");
        telemetry.update();

        waitForStart();
        dropLevel = tsePipeline.getDropLevel();

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

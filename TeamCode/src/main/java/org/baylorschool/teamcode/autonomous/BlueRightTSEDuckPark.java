package org.baylorschool.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Sensors;
import org.baylorschool.library.TSEPipeline;
import org.baylorschool.library.TwoBarLift;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "BlueRightTSEDuckPark", group = "BlueRight")
public class BlueRightTSEDuckPark extends LinearOpMode {

    private TwoBarLift twoBarLift;
    private Sensors sensors;
    private TSEPipeline tsePipeline;
    private OpenCvWebcam webcam;

    private Location currentLocation = Places.blueRightStart;

    private Globals.DropLevel dropLevel;

    @Override
    public void runOpMode() {
        twoBarLift = new TwoBarLift(this);
        sensors = new Sensors(hardwareMap, false);

        tsePipeline = new TSEPipeline(Places.StartLocation.BLUE_RIGHT);
        webcam = TSEPipeline.openWebcam(this, tsePipeline);

        waitForStart();

        dropLevel = tsePipeline.getDropLevel();
        webcam.stopStreaming();
        webcam.closeCameraDevice();

        telemetry.addData("Level", dropLevel.name());
        twoBarLift.moveToDropLevel(dropLevel);

        twoBarLift.startThread();
        while (opModeIsActive()) {}
        twoBarLift.closeThread();
    }
}
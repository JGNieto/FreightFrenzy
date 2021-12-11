package org.baylorschool.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.DropDuck;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.Location;
import org.baylorschool.library.Sensors;
import org.baylorschool.library.TSEPipeline;
import org.baylorschool.library.TwoBarLift;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.Arrays;
import java.util.Collections;

@Autonomous(name = "BlueRightTSEDuckPark", group = "BlueRight")
public class BlueRightTSEDuckPark extends LinearOpMode {

    private TwoBarLift twoBarLift;
    private Sensors sensors;
    private TSEPipeline tsePipeline;
    private OpenCvWebcam webcam;
    private Location currentLocation = Places.blueRightStart;
    private Globals.DropLevel dropLevel;
    private Carousel carousel;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Loading...");
        telemetry.update();
        twoBarLift = new TwoBarLift(this);
        sensors = new Sensors(hardwareMap, false);
        sensors.initialize(hardwareMap, currentLocation.getHeading());
        carousel = new Carousel(hardwareMap);

        tsePipeline = new TSEPipeline(Places.StartLocation.BLUE_RIGHT, this);
        webcam = TSEPipeline.openWebcam(this, tsePipeline);

        telemetry.addData("Status", "Ready");
        telemetry.update();

        waitForStart();
        sensors.getMecanum().resetEncoders();
        sensors.getMecanum().updateEncoderReadings();
        dropLevel = tsePipeline.getDropLevel();
        webcam.stopStreaming();
        webcam.closeCameraDevice();

        twoBarLift.startThread();

        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.BlueRightToHub), this);
        twoBarLift.moveToDropLevel(dropLevel);
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(twoBarLift.getScoringLocation(currentLocation, TwoBarLift.Hub.BLUE, dropLevel)), this);
        twoBarLift.releaseItem();
        Location newTarget = new Location(currentLocation.getX(), currentLocation.getY(), currentLocation.getHeading() - 50);
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(newTarget), this);
        twoBarLift.retract();
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.BlueRightHubToCarousel), this);
        DropDuck.dropTheDuck(Carousel.CarouselSide.BLUE, sensors.getMecanum(), this, carousel, false);
        twoBarLift.closeThread();
    }
}

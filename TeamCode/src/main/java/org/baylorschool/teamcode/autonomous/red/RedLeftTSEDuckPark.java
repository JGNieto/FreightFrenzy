package org.baylorschool.teamcode.autonomous.red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.DropDuck;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.Location;
import org.baylorschool.library.Odometry;
import org.baylorschool.library.Sensors;
import org.baylorschool.library.TSEPipeline;
import org.baylorschool.library.lift.TwoBarLift;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.Arrays;
import java.util.Collections;

@Autonomous(name = "RedLeftTSEDuckPark", group = "Red")
public class RedLeftTSEDuckPark extends LinearOpMode {

    private TwoBarLift twoBarLift;
    private Sensors sensors;
    private TSEPipeline tsePipeline;
    private OpenCvWebcam webcam;
    private Location currentLocation = Places.redLeftStart;
    private Globals.DropLevel dropLevel;
    private Carousel carousel;
    private Odometry odometry;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Loading...");
        telemetry.update();
        odometry = new Odometry(hardwareMap, true);
        twoBarLift = new TwoBarLift(this);
        sensors = new Sensors(hardwareMap, false);
        sensors.initialize(hardwareMap, currentLocation.getHeading());
        carousel = new Carousel(hardwareMap);

        tsePipeline = new TSEPipeline(this);
        webcam = TSEPipeline.openWebcam(this, tsePipeline);
        twoBarLift.initialize();

        telemetry.addData("Status", "Waiting for vision...");

        telemetry.update();

        waitForStart();
        sensors.getMecanum().resetEncoders();
        sensors.getMecanum().updateEncoderReadings();
        dropLevel = tsePipeline.getDropLevel();
        webcam.stopStreaming();
        webcam.closeCameraDevice();

        twoBarLift.startThread();

        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.RedLeftToHub), this);
        twoBarLift.moveToDropLevel(dropLevel);
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(twoBarLift.getScoringLocation(currentLocation, TwoBarLift.Hub.RED, dropLevel)), this);
        twoBarLift.releaseItem();

        // Make sure to turn before dropping lift.
//        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(new Location(currentLocation).setHeading(180)), this);
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(new Location(currentLocation).setHeading(0)), this);
        twoBarLift.retract();

        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.RedLeftHubToCarousel), this);
        DropDuck.dropTheDuck(Carousel.CarouselSide.RED, sensors.getMecanum(), this, carousel, false);
        MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.CarouselToRedPark), this);
        twoBarLift.closeThread();
    }
}

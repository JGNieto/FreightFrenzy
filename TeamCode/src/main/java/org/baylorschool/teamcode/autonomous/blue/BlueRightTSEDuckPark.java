package org.baylorschool.teamcode.autonomous.blue;

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

@Autonomous(name = "BlueRightTSEDuckPark", group = "Blue")
public class BlueRightTSEDuckPark extends LinearOpMode {

    private TwoBarLift twoBarLift;
    private Sensors sensors;
    private TSEPipeline tsePipeline;
    private OpenCvWebcam webcam;
    private Location currentLocation = new Location(Places.blueRightStart);
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
        twoBarLift.moveDown();

        telemetry.addData("Status", "Waiting for vision...");

        telemetry.update();

        waitForStart();
        sensors.getMecanum().resetEncoders();
        sensors.getMecanum().updateEncoderReadings();
        dropLevel = tsePipeline.getDropLevel();
        webcam.stopStreaming();
        webcam.closeCameraDevice();

        twoBarLift.initialize();
        twoBarLift.startThread();

        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.BlueRightToHub), this);
        twoBarLift.moveToDropLevel(dropLevel);
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(twoBarLift.getScoringLocation(currentLocation, TwoBarLift.Hub.BLUE, dropLevel)), this);
        twoBarLift.releaseItem();

        // Make sure to turn before dropping lift.
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(new Location(currentLocation).forward().setHeading(180)), this);
        twoBarLift.retract();

        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.BlueRightHubToCarousel), this);
        DropDuck.dropTheDuck(Carousel.CarouselSide.BLUE, sensors.getMecanum(), this, carousel, false);
        MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.CarouselToBluePark), this);
        twoBarLift.closeThread();
    }
}

package org.baylorschool.teamcode.autonomous.encoders.blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.DropDuck;
import org.baylorschool.actions.MoveWaypointsEncoders;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.Location;
import org.baylorschool.library.Sensors;
import org.baylorschool.library.TSEPipeline;
import org.baylorschool.library.lift.Lift;
import org.baylorschool.library.lift.TwoBarLift;
import org.baylorschool.library.localization.Odometry;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.Arrays;
import java.util.Collections;

@Autonomous(name = "BlueRightTSEDuckPark", group = "Blue")
public class BlueRightTSEDuckPark extends LinearOpMode {

    private TwoBarLift twoBarLift;
    private Sensors sensors;
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

        twoBarLift.moveDown(this);

        telemetry.addData("Status", "Waiting for vision...");

        telemetry.update();

        dropLevel = Globals.DropLevel.MIDDLE;
        waitForStart();
        sensors.getMecanum().resetEncoders();
        sensors.getMecanum().updateEncoderReadings();

        twoBarLift.initialize();
        twoBarLift.startThread();

        twoBarLift.moveToDropLevel(dropLevel);
        currentLocation = MoveWaypointsEncoders.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.BlueRightToHub), this);
       // currentLocation = MoveWaypointsEncoders.moveToWaypoints(currentLocation, sensors, Collections.singletonList(twoBarLift.getScoringLocation(currentLocation, TwoBarLift.Hub.COOP, dropLevel)), this);
        twoBarLift.releaseItem();

        // Make sure to turn before dropping lift.
        twoBarLift.retract();

        currentLocation = MoveWaypointsEncoders.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.BlueRightHubToCarousel), this);
        DropDuck.dropTheDuck(Carousel.CarouselSide.BLUE, sensors.getMecanum(), this, carousel, false);
        MoveWaypointsEncoders.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.CarouselToBluePark), this);
        twoBarLift.closeThread();
    }
}

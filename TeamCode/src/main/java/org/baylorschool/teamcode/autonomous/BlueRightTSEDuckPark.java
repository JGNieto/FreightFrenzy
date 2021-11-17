package org.baylorschool.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.DropDuck;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
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
        twoBarLift = new TwoBarLift(this);
        sensors = new Sensors(hardwareMap, false);

        tsePipeline = new TSEPipeline(Places.StartLocation.BLUE_RIGHT);
        webcam = TSEPipeline.openWebcam(this, tsePipeline);
        carousel = new Carousel(hardwareMap);

        waitForStart();

        dropLevel = tsePipeline.getDropLevel();
        webcam.stopStreaming();
        webcam.closeCameraDevice();

        telemetry.addData("Level", dropLevel.name());
        twoBarLift.moveToDropLevel(dropLevel);

        twoBarLift.startThread();
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.BlueRightToHub), this);
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(TwoBarLift.getScoringLocation(currentLocation, TwoBarLift.Hub.BLUE, dropLevel)), this);
        twoBarLift.releaseItem();
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.BlueRightHubToCarousel), this);
        currentLocation = DropDuck.dropTheDuck(Carousel.CarouselSide.BLUE, sensors.getMecanum(), this, carousel, false);
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.CarouselToBluePark), this);
        twoBarLift.closeThread();
    }
}

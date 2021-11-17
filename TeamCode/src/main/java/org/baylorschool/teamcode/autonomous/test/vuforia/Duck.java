package org.baylorschool.teamcode.autonomous.test.vuforia;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.actions.DropDuck;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.Vuforia;

import java.util.Arrays;

@Autonomous(name="Duck", group ="Test")
public class Duck extends LinearOpMode {

    private Location currentLocation = new Location(-609.6, 1568.8, 0, 0, 0, -90);
    private Vuforia vuforia;
    private Mecanum mecanum;
    private IMU imu;
    private Carousel carousel;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Getting ready");
        telemetry.update();
        vuforia = new Vuforia(hardwareMap);
        vuforia.initializeParamers(false);


        mecanum = new Mecanum(hardwareMap);
        carousel = new Carousel(hardwareMap);
        imu = new IMU();

        imu.initializeImu(hardwareMap);
        imu.forceValue(currentLocation.getHeading());

        vuforia.startTracking();

        telemetry.addData("Status", "Ready!");
        telemetry.update();

        waitForStart();
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        mecanum.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mecanum.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        currentLocation = DropDuck.dropTheDuck(Carousel.CarouselSide.RED, mecanum, this, carousel, true);

        vuforia.stopTracking();
    }
}

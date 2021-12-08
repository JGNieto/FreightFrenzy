package org.baylorschool.teamcode.autonomous.test.vuforia;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.actions.DropDuck;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;

@Autonomous(name="Duck", group ="Test")
public class Duck extends LinearOpMode {

    private Location currentLocation = new Location(-609.6, 1568.8, 0, 0, 0, -90);
    private Mecanum mecanum;
    private IMU imu;
    private Carousel carousel;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Getting ready");
        telemetry.update();


        mecanum = new Mecanum(hardwareMap);
        carousel = new Carousel(hardwareMap);
        imu = new IMU();

        imu.initializeImu(hardwareMap);
        imu.forceValue(currentLocation.getHeading());

        telemetry.addData("Status", "Ready!");
        telemetry.update();

        waitForStart();
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        mecanum.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mecanum.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        currentLocation = DropDuck.dropTheDuck(Carousel.CarouselSide.BLUE, mecanum, this, carousel, true);
    }
}

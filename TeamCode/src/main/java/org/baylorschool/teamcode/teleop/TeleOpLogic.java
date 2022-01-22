package org.baylorschool.teamcode.teleop;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.Globals;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.ControlMap;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Odometry;
import org.baylorschool.library.Sounds;
import org.baylorschool.library.Tape;
import org.baylorschool.library.lift.Lift;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TeleOpLogic extends LinearOpMode {

    private Mecanum mecanum;
    private Carousel carousel;
    private Lift lift;
    private Odometry odometry;
    private final ControlMap controlMap;
    private Tape tape;
    private Sounds sounds;

    private final double SLOW_MODE_COEFFICIENT = 0.5;
    private final double ROTATION_COEFFICIENT = 0.8;

    public TeleOpLogic(ControlMap controlMap) {
        super();
        this.controlMap = controlMap;
    }

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        odometry = new Odometry(hardwareMap, true);
        mecanum = new Mecanum(hardwareMap);
        carousel = new Carousel(hardwareMap);
        tape = new Tape(hardwareMap);
        lift = Globals.createNewLift(this);
        lift.initialize();

        waitForStart();
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        controlMap.setGamepad1(gamepad1);
        controlMap.setGamepad2(gamepad2);

        while (opModeIsActive()) {
            double y = controlMap.getY();
            double x = controlMap.getX();
            double rotation = controlMap.getRotation() * ROTATION_COEFFICIENT;

            if (controlMap.carouselBlue())
                //carousel.dropDuckAsync(Carousel.CarouselSide.BLUE);
                carousel.setDropPower(Carousel.CarouselSide.BLUE);
            else if (controlMap.carouselRed())
                //carousel.dropDuckAsync(Carousel.CarouselSide.RED);
                carousel.setDropPower(Carousel.CarouselSide.RED);
            else
                carousel.stop();

            // If driver wants full power, give it to them.
            if (controlMap.fullRotationPower())
                rotation = controlMap.getRotation();

            // Tape measure power.
            tape.setTiltPower(controlMap.tapeTilt());
            tape.setExtendPower(controlMap.tapeExtend());

            // Execute movement
            mecanum.moveGamepad(y, x, rotation, controlMap.isSlowMode() ? SLOW_MODE_COEFFICIENT : 1);
            lift.loopIterationTeleOp(controlMap);
            // Report telemetry
            telemetry.addData("X Gamepad", x);
            telemetry.addData("Y Gamepad", y);
            telemetry.addData("Speed", y);
            telemetry.addData("Strafe", x);
            telemetry.addData("Rotation", rotation);
            telemetry.addData("Tape Tilt Power", controlMap.tapeTilt());
            telemetry.addData("Tape Tilt", tape.getCurrentTilt());
            telemetry.addData("Tape Extend", controlMap.tapeExtend());
            telemetry.addData("EncoderFR", mecanum.getFrMotor().getCurrentPosition());
            telemetry.addData("EncoderBR", mecanum.getBrMotor().getCurrentPosition());
            telemetry.addData("EncoderFL", mecanum.getFlMotor().getCurrentPosition());
            telemetry.addData("EncoderBL", mecanum.getBlMotor().getCurrentPosition());
            telemetry.addData("Carousel Blue", controlMap.carouselBlue());
            telemetry.addData("Carousel Red", controlMap.carouselRed());
            telemetry.update();
        }

        lift.closeThread();
    }
}

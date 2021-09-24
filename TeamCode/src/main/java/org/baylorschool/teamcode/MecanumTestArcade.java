package org.baylorschool.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.baylorschool.movement.Mecanum;

@TeleOp(name="MecanumTestArcade", group="Test")
public class MecanumTestArcade extends LinearOpMode {

    private Mecanum mecanum;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        mecanum = new Mecanum(hardwareMap, Mecanum.Side.RIGHT,
                "blMotor", "flMotor", "brMotor", "frMotor");

        waitForStart();

        while (opModeIsActive()) {
            mecanum.move(gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.left_stick_x);
        }
    }
}

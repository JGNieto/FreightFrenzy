package org.baylorschool.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

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

        boolean slowMode = false;

        while (opModeIsActive()) {
            double y = gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rotation = gamepad1.right_stick_x;

            slowMode = slowModeToggle(gamepad1, slowMode);

            telemetry.addData("Speed", y);
            telemetry.addData("Strafe", x);
            telemetry.addData("Rotation", rotation);
            telemetry.update();

            mecanum.moveGamepad(y, x, rotation);
        }
    }

    private boolean slowModeToggle(Gamepad gamepad, boolean current) {
        if (gamepad.left_bumper) {
            return true;
        }

        if (gamepad.right_bumper) {
            return false;
        }

        return current;
    }

    private boolean slowModeHold(Gamepad gamepad) {
        return gamepad.left_bumper;
    }
}

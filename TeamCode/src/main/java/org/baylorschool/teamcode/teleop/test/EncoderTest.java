package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Disabled
@TeleOp(name="EncoderTest", group="Test")
public class EncoderTest extends LinearOpMode {

    private DcMotor flMotor = null;
    private DcMotor frMotor = null;
    private DcMotor blMotor = null;
    private DcMotor brMotor = null;
    final int target = -616;

    @Override
    public void runOpMode() {
        flMotor = hardwareMap.get(DcMotor.class, "flMotor");
        frMotor = hardwareMap.get(DcMotor.class, "frMotor");
        brMotor = hardwareMap.get(DcMotor.class, "brMotor");
        blMotor = hardwareMap.get(DcMotor.class, "blMotor");

        frMotor.setDirection(DcMotor.Direction.REVERSE);
        brMotor.setDirection(DcMotor.Direction.REVERSE);

        flMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        blMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        brMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        waitForStart();

        while (opModeIsActive()) {
            if (!(frMotor.isBusy() || flMotor.isBusy() || brMotor.isBusy() || blMotor.isBusy())) {
                flMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                frMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                blMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                brMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                flMotor.setTargetPosition(target);
                frMotor.setTargetPosition(target);
                blMotor.setTargetPosition(target);
                brMotor.setTargetPosition(target);

                flMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                frMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                blMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                brMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                flMotor.setPower(0.4);
                frMotor.setPower(0.4);
                blMotor.setPower(0.4);
                brMotor.setPower(0.4);
            }

            telemetry.addData("fl", flMotor.getCurrentPosition());
            telemetry.addData("fr", frMotor.getCurrentPosition());
            telemetry.addData("bl", blMotor.getCurrentPosition());
            telemetry.addData("br", brMotor.getCurrentPosition());
            telemetry.update();
        }
    }
}

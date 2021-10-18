package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name="EncoderTest", group="Test")
public class EncoderTest extends LinearOpMode {

    private DcMotor flMotor = null;
    private DcMotor frMotor = null;
    private DcMotor blMotor = null;
    private DcMotor brMotor = null;
    final int target = 6161;

    @Override
    public void runOpMode() {
        flMotor = hardwareMap.get(DcMotor.class, "FLMotor");
        frMotor = hardwareMap.get(DcMotor.class, "FRMotor");
        brMotor = hardwareMap.get(DcMotor.class, "BRMotor");
        blMotor = hardwareMap.get(DcMotor.class, "BLMotor");

        flMotor.setDirection(DcMotor.Direction.REVERSE);
        frMotor.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        flMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        blMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        brMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        flMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        blMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        brMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        flMotor.setTargetPosition(target);
        frMotor.setTargetPosition(target);
        blMotor.setTargetPosition(target);
        brMotor.setTargetPosition(target);

        flMotor.setPower(0.7);
        frMotor.setPower(0.7);
        blMotor.setPower(0.7);
        brMotor.setPower(0.7);

        while (opModeIsActive()) {
        }
    }
}

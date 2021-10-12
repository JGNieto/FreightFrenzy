package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.baylorschool.library.Vuforia;

@TeleOp(name="VuforiaNavigationTest", group ="Test")
public class VuforiaNavigationTest extends LinearOpMode {

    private Vuforia vuforia;

    @Override
    public void runOpMode() {
        vuforia = new Vuforia(hardwareMap);
        vuforia.initializeParamers(true);

        waitForStart();
        vuforia.startTracking();

        while (opModeIsActive()) {
            vuforia.lookForTargets(telemetry);
        }

        vuforia.stopTracking();
    }


}

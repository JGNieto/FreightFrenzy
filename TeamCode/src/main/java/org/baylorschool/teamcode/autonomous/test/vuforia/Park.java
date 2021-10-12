package org.baylorschool.teamcode.autonomous.test.vuforia;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.library.Location;
import org.baylorschool.library.Vuforia;

@Autonomous(name="Park", group ="VuforiaTest")
public class Park extends LinearOpMode {

    private Vuforia vuforia;
    private Location currentLocation;

    @Override
    public void runOpMode() {
        vuforia = new Vuforia(hardwareMap);
        vuforia.initializeParamers(true);

        waitForStart();
        vuforia.startTracking();

        while (opModeIsActive()) {
            Location location = vuforia.lookForTargets(telemetry);
            if (location != null) {
                currentLocation = location;
            }
            telemetry.update();
        }

        vuforia.stopTracking();
    }


}

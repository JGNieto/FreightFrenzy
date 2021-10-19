package org.baylorschool.teamcode.autonomous.test.vuforia;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.library.Location;
import org.baylorschool.library.Path;
import org.baylorschool.library.Vuforia;

import java.util.Arrays;

@Autonomous(name="Park", group ="VuforiaTest")
public class Park extends LinearOpMode {

    private Vuforia vuforia;
    private Location currentLocation;
    private Location startLocation;

    private Path path;

    @Override
    public void runOpMode() {
        vuforia = new Vuforia(hardwareMap);
        vuforia.initializeParamers(true);

        Location[] locations = new Location[]{

        };

        path = new Path(Arrays.asList(locations), new Location(0,0,0,0,0,0));

        waitForStart();
        vuforia.startTracking();

        while (opModeIsActive()) {

            telemetry.update();
        }

        vuforia.stopTracking();
    }

    private void turnToHeading(double heading) {

    }

}

package org.baylorschool.library.lift;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.library.Location;
import org.baylorschool.library.localization.Localization;

public class CascadingLift extends Lift {

    // FIXME
    public CascadingLift(LinearOpMode opMode) {
        super(opMode);
    }

    @Override
    public void moveToDropLevel(Globals.DropLevel dropLevel) {

    }

    @Override
    public void releaseItem() {

    }

    @Override
    public Location releaseItemLocalization(Location currentLocation, Localization localization) {
        return null;
    }

    @Override
    public void retract() {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void initializeSync(LinearOpMode opMode) {
    }

    @Override
    public void loopIteration() {

    }

    @Override
    public int getCapturedElements() {
        return 0;
    }

    @Override
    public void moveDown(LinearOpMode opMode) {

    }

    @Override
    public Location getScoringLocation(Location currentLocation, Hub hub, Globals.DropLevel dropLevel) {
        return null;
    }
}

package org.baylorschool.library;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;

public class Path {

    private ArrayList<Location> locations;
    private Location tolerance;

    public Path(List<Location> locations, Location tolerance) {
        this.locations = new ArrayList<>(locations);
        this.tolerance = tolerance;
    }

    public void checkGoal(Location robotLocation) {
        Location difference = Location.difference(currentGoal(), robotLocation);
        if (!(
            (difference.getX() > tolerance.getX() && tolerance.getX() != -1) ||
            (difference.getY() > tolerance.getY() && tolerance.getY() != -1) ||
            (difference.getZ() > tolerance.getZ() && tolerance.getZ() != -1) ||
            (difference.getRoll() > tolerance.getRoll() && tolerance.getRoll() != -1) ||
            (difference.getPitch() > tolerance.getPitch() && tolerance.getPitch() != -1) ||
            (difference.getHeading() > tolerance.getHeading() && tolerance.getHeading() != -1)
        )) {
            locations.remove(0);
        }
    }

    public void checkGoalTelemetry(Location robotLocation, Telemetry telemetry) {
        Location difference = Location.difference(currentGoal(), robotLocation);
        telemetry.addData("X Diff", difference.getX());
        telemetry.addData("Y Diff", difference.getY());
        telemetry.addData("Heading Diff", difference.getHeading());
    }

    public Location currentGoal() {
        if (locations.size() > 0)
            return locations.get(0);
        else
            return null;
    }

    public Location getTolerance() {
        return tolerance;
    }

    public List<Location> getLocations() {
        return locations;
    }
}

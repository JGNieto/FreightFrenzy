package org.baylorschool.library;

import java.util.List;

public class Path {

    private List<Location> locations;
    private Location tolerance;

    public Path(List<Location> locations, Location tolerance) {
        this.locations = locations;
        this.tolerance = tolerance;
    }

    public void checkGoal(Location robotLocation) {
        Location difference = Location.difference(currentGoal(), robotLocation);
        if (!(
            (difference.getX() > tolerance.getX() && difference.getX() != -1) ||
            (difference.getY() > tolerance.getY() && difference.getY() != -1) ||
            (difference.getZ() > tolerance.getZ() && difference.getZ() != -1) ||
            (difference.getRoll() > tolerance.getRoll() && difference.getRoll() != -1) ||
            (difference.getPitch() > tolerance.getPitch() && difference.getPitch() != -1) ||
            (difference.getHeading() > tolerance.getHeading() && difference.getHeading() != -1)
        )) {
            locations.remove(0);
        }
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
}

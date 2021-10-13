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
            difference.getX() > tolerance.getX() ||
            difference.getY() > tolerance.getY() ||
            difference.getZ() > tolerance.getZ() ||
            difference.getRoll() > tolerance.getRoll() ||
            difference.getPitch() > tolerance.getPitch() ||
            difference.getHeading() > tolerance.getHeading()
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
}

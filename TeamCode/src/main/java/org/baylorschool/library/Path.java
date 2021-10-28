package org.baylorschool.library;

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
        // TODO: Check for goals in the future, to cut corners.
        Location difference = Location.difference(currentGoal(), robotLocation);
        if (difference.getX() < tolerance.getX() && difference.getY() < tolerance.getY()) {
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

    public List<Location> getLocations() {
        return locations;
    }
}

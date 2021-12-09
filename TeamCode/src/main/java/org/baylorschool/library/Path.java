package org.baylorschool.library;

import org.baylorschool.Globals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Path {

    private ArrayList<Location> locations;
    private Location tolerance;
    private Location previousLocation = null;

    public Path(List<Location> locations) {
        this.locations = new ArrayList<>(locations);
        this.tolerance = Globals.defaultTolerance;
    }

    public Path(Location[] locations) {
        this.locations = new ArrayList<>(Arrays.asList(locations));
        this.tolerance = Globals.defaultTolerance;
    }

    public Path(List<Location> locations, Location tolerance) {
        this.locations = new ArrayList<>(locations);
        this.tolerance = tolerance;
    }

    public Path(Location[] locations, Location tolerance) {
        this.locations = new ArrayList<>(Arrays.asList(locations));
        this.tolerance = tolerance;
    }

    public void checkGoal(Location robotLocation) {
        // TODO: Check for goals in the future, to cut corners.
        Location difference = Location.difference(currentGoal(), robotLocation);
        if (difference.getX() < tolerance.getX() && difference.getY() < tolerance.getY()) {
            previousLocation = locations.get(0);
            locations.remove(0);
        }
    }

    public Location getLastLocation() {
        return locations.get(locations.size() - 1);
    }

    public void initialLocation(Location initialLocation) {
        if (!locations.get(0).samePlanePlaceAs(initialLocation))
            locations.add(0, initialLocation);
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

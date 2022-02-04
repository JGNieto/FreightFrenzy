package org.baylorschool.library;

import org.baylorschool.Globals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Path {
    // The purpose of this class is to encapsulate the data of a path with multiple locations and provide some utils.

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

    public Path(Location location) {
        this.locations = new ArrayList<>();
        locations.add(location);
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

    public boolean checkGoal(Location robotLocation) {
        // TODO: Check for goals in the future, to cut corners.
        Location difference = Location.difference(currentGoal(), robotLocation);
        boolean hasRemoved = false;
        if (difference.getX() < tolerance.getX() && difference.getY() < tolerance.getY()) {
            previousLocation = locations.get(0);
            if (previousLocation.getRunnable() != null)
                previousLocation.getRunnable().run();
            locations.remove(0);
            hasRemoved = true;
        }
        return hasRemoved;
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

    /**
     * Sets the runnable for a location in the path.
     * @param i Index of the location in the path.
     */
    public Path setRunnable(int i, Runnable runnable) {
        locations.get(i).setRunnable(runnable);
        return this;
    }

    public void setTolerance(Location tolerance) {
        this.tolerance = tolerance;
    }

    public Location getTolerance() {
        return tolerance;
    }

    public List<Location> getLocations() {
        return locations;
    }
}

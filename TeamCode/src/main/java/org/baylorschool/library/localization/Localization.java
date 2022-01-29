package org.baylorschool.library.localization;

import org.baylorschool.library.Location;

public interface Localization {
    Location calculateNewLocation(Location currentLocation);

    void setBackwards(boolean backwards);
    boolean isBackwards();
}

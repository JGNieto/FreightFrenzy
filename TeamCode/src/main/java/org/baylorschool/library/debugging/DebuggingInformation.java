package org.baylorschool.library.debugging;

import org.baylorschool.library.Location;

public
class DebuggingInformation {
    protected static Location location;

    public static void setLocation(Location location) {
        DebuggingInformation.location = location;
    }
}

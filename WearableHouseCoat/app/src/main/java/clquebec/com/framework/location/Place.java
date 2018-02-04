package clquebec.com.framework.location;

import java.util.Set;
import java.util.UUID;

import clquebec.com.framework.controllable.ControllableDevice;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public abstract class Place {
    public abstract String getName();

    public abstract UUID getID();

    public abstract Set<ControllableDevice> getDevices();

    public boolean equals(Object other) {
        return other instanceof Place && ((Place) other).getID().equals(this.getID());
    }
}

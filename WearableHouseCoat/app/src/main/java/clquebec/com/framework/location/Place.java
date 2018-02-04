package clquebec.com.framework.location;

import java.util.Set;
import java.util.UUID;

import clquebec.com.framework.controllable.ControllableDevice;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public interface Place {
    String getName();

    UUID getID();

    Set<ControllableDevice> getDevices();
}

package com.clquebec.framework.listenable;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 23/02/18
 */

public interface ListenableDevice {
    //Add / remove from a set of listeners
    void addListener(DeviceChangeListener listener);

    void removeListener(DeviceChangeListener listener);
}

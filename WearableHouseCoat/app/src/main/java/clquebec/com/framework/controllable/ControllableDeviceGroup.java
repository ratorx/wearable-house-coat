package clquebec.com.framework.controllable;

import java.util.Set;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 04/02/18
 */

public interface ControllableDeviceGroup extends ControllableDevice {
    Set<ControllableDevice> getDevices();
}

package clquebec.com.framework.controllable;

import java.util.List;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 04/02/18
 */

public interface ControllableDeviceGroup extends ControllableDevice {
    List<ControllableDevice> getDevices();
}

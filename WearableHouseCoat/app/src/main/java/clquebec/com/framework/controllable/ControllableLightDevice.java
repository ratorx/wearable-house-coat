package clquebec.com.framework.controllable;

import android.graphics.Color;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 *
 * An interface for control of any lighting device.
 */

public interface ControllableLightDevice extends ControllableDevice {
    //Allows setting of colour
    void setLightColor(Color c) throws ActionNotSupported;
}

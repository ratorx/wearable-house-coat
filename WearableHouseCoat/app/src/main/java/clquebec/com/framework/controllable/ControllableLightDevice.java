package clquebec.com.framework.controllable;


/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 * <p>
 * An interface for control of any lighting device.
 */

public interface ControllableLightDevice extends ControllableDevice {
    //Allows setting of colour
    void setLightColor(int color) throws ActionNotSupported;
}

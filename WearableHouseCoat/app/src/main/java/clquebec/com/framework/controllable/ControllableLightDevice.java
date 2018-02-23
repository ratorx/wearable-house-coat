package clquebec.com.framework.controllable;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 *
 * An interface for control of any lighting device.
 */

public interface ControllableLightDevice extends ControllableDevice {
    //Allows setting of colour
    void setLightColor(int color) throws ActionNotSupported;

    int getLightColor() throws ActionNotSupported;

    boolean setBrightness(int brightness) throws ActionNotSupported;

    int getBrightness() throws ActionNotSupported;
}

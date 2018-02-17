package clquebec.com.framework.controllable;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 * <p>
 * An interface for controlling any device with controllable temperature.
 */

public interface ControllableHeatDevice extends ControllableDevice {

    //Set temperature to given value, returns success
    boolean setTemperature(float heat);

    //Get current temperature
    float getTemperature();
}

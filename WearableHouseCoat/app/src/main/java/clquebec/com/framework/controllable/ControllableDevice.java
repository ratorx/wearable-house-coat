package clquebec.com.framework.controllable;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 * <p>
 * An interface for any device that's controllable.
 * Useful for keeping controllable devices in the same data structure
 */

public interface ControllableDevice {
    //Turn device on - returns success or failure.
    boolean enable();

    //Turn device off - returns success or failure.
    boolean disable();

    //Check device state
    boolean isEnabled();

    void setName(String name);

    String getName();

    //Mostly used for icons, but also for type names
    ControllableDeviceType getType();

    //The code that should be run in the case of a quick action.
    //Called by DeviceControlButton
    boolean quickAction();

    //The code that should be run in the case of an extended action.
    //Called by DeviceControlButton
    boolean extendedAction();
}

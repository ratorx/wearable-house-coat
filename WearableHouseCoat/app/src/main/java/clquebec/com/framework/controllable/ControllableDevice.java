package clquebec.com.framework.controllable;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 *
 * An interface for any device that's controllable.
 * Useful for keeping controllable devices in the same data structure
 */

public interface ControllableDevice {
    //Turn device on - returns success or failure.
    boolean enable();

    //Turn device off - returns success or failure.
    boolean disable();
}

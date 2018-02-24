package com.clquebec.framework.controllable;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public interface ControllableCurtainDevice extends ControllableDevice {
    void open();

    void close();

    void openFraction(float fraction) throws ActionNotSupported;
}

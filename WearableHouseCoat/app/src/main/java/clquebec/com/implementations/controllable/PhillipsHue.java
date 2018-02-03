package clquebec.com.implementations.controllable;

import android.graphics.Color;

import clquebec.com.framework.controllable.ActionNotSupported;
import clquebec.com.framework.controllable.ControllableLightDevice;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class PhillipsHue implements ControllableLightDevice {
    @Override
    public boolean isLightOn() {
        return false;
    }

    @Override
    public void setLightColor(Color c) throws ActionNotSupported {

    }

    @Override
    public boolean enable() {
        return false;
    }

    @Override
    public boolean disable() {
        return false;
    }
}

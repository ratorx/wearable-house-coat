package clquebec.com.implementations.controllable;

import android.content.Context;

import org.json.JSONObject;

import clquebec.com.framework.controllable.ActionNotSupported;
import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllableDeviceType;
import clquebec.com.framework.controllable.ControllableLightDevice;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class PhilipsHue implements ControllableLightDevice {
    //TODO: Implement this.

    public PhilipsHue(Context c) {
        //Scan for Hues on local network

        //Pick the right one(s)

        //Initialise internal state.
    }

    @Override
    public void setLightColor(int color) throws ActionNotSupported {

    }

    @Override
    public boolean enable() {
        return false;
    }

    @Override
    public boolean disable() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ControllableDeviceType getType() {
        return ControllableDeviceType.LIGHT;
    }

    @Override
    public boolean quickAction() {
        return false;
    }

    @Override
    public boolean extendedAction() {
        return false;
    }

    @Override
    public ControllableDevice getDeviceInstance(Context context, JSONObject config) {
        return null;
    }
}

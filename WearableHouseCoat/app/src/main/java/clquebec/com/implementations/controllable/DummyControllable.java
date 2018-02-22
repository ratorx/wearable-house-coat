package clquebec.com.implementations.controllable;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllableDeviceType;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 21/02/18
 */

public class DummyControllable implements ControllableDevice {
    //This class is for testing. It contains no implementations.
    //Since Controllable classes are loaded dynamically, this is required.
    //Tests will probably fail if you remove this.

    private UUID mUUID;

    public DummyControllable(Context c, UUID id, JSONObject config) throws JSONException{
        //Test that the location string is given.
        mUUID = id;
    }

    @Override
    public boolean enable() {
        return true;
    }

    @Override
    public boolean disable() {
        return true;
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
        return "";
    }

    @Override
    public ControllableDeviceType getType() {
        return ControllableDeviceType.LIGHT;
    }

    @Override
    public UUID getID() {
        return mUUID;
    }

    @Override
    public boolean quickAction() {
        return false;
    }

    @Override
    public boolean extendedAction() {
        return false;
    }
}

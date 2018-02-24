package com.clquebec.implementations.controllable;

import android.content.Context;

import com.clquebec.framework.controllable.ControllableDevice;
import com.clquebec.framework.controllable.ControllableDeviceType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

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

    @Override
    public boolean isConnected(){
        return false;
    }
}

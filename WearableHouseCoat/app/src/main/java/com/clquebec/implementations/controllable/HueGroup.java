package com.clquebec.implementations.controllable;

import android.content.Context;
import android.content.Intent;

import com.clquebec.framework.controllable.ActionNotSupported;
import com.clquebec.framework.controllable.ControllableDeviceType;
import com.clquebec.framework.controllable.ControllableLightDevice;
import com.clquebec.framework.listenable.DeviceChangeListener;
import com.clquebec.framework.listenable.ListenableDevice;
import com.clquebec.wearablehousecoat.LightControlPanelActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 28/02/18
 */

public class HueGroup implements ControllableLightDevice, ListenableDevice {
    private UUID mUUID;
    private Context mContext;
    private String mName = "Hue Group";
    private List<PhilipsHue> mPhilipsHues = new ArrayList<>();

    public HueGroup(Context c, UUID id, JSONObject config) throws JSONException {
        mContext = c;
        mUUID = id;

        //Loop through each light in the JSON, and get the PhilipsHue of that
        //TODO: not reinstantiate Hue devices?
        JSONArray lights = config.getJSONArray("lights");

        for(int i = 0; i < lights.length(); i++){
            int lightId = lights.getInt(i);

            JSONObject lightConfig = new JSONObject();
            lightConfig.put("light", lightId);

            mPhilipsHues.add(new PhilipsHue(c, UUID.randomUUID(), lightConfig));
        }
    }

    @Override
    public void addListener(DeviceChangeListener listener) {
        for(PhilipsHue p : mPhilipsHues){
            p.addListener(listener);
        }
    }

    @Override
    public void removeListener(DeviceChangeListener listener) {
        for(PhilipsHue p : mPhilipsHues){
            p.removeListener(listener);
        }
    }

    @Override
    public void setLightColor(Integer color) throws ActionNotSupported {
        for(PhilipsHue p : mPhilipsHues){
            p.setLightColor(color);
        }
    }

    @Override
    public int getLightColor() throws ActionNotSupported {
        //Assume all are the same colour
        return mPhilipsHues.get(0).getLightColor();
    }

    @Override
    public boolean setBrightness(Integer brightness) throws ActionNotSupported {
        boolean success = true;
        for(PhilipsHue p : mPhilipsHues){
            success = success && p.setBrightness(brightness);
        }
        return success;
    }

    @Override
    public int getBrightness() throws ActionNotSupported {
        //Assume all are the same brightness
        return mPhilipsHues.get(0).getBrightness();
    }

    @Override
    public boolean enable() {
        boolean success = true;
        for(PhilipsHue p : mPhilipsHues){
            success = success && p.enable();
        }
        return success;
    }

    @Override
    public boolean disable() {
        boolean success = true;
        for(PhilipsHue p : mPhilipsHues){
            success = success && p.disable();
        }
        return success;
    }

    @Override
    public boolean isEnabled() {
        boolean success = true;
        for(PhilipsHue p : mPhilipsHues){
            success = success && p.isEnabled();
        }
        return success;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public String getName() {
        return mName;
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
        return isEnabled() ? disable() : enable();
    }

    @Override
    public boolean extendedAction() {
        Intent lightControls = new Intent(mContext, LightControlPanelActivity.class);
        lightControls.putExtra(LightControlPanelActivity.ID_EXTRA, this.getID());
        mContext.startActivity(lightControls);

        return true;
    }

    @Override
    public boolean isConnected() {
        boolean success = true;
        for(PhilipsHue p : mPhilipsHues){
            success = success && p.isConnected();
        }
        return success;
    }
}

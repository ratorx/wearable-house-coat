package com.clquebec.implementations.controllable;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.clquebec.framework.IFTTT;
import com.clquebec.framework.controllable.ActionNotSupported;
import com.clquebec.framework.controllable.ControllableDeviceType;
import com.clquebec.framework.controllable.ControllableLightDevice;
import com.clquebec.wearablehousecoat.LightControlPanelActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class IFTTTLight implements ControllableLightDevice {
    private static final String TAG = "IFTTTLight";
    private static final String EVENT_PREFIX = "light_";

    private String mName = null;
    private boolean mCurrentState;
    private IFTTT mIFTTT;
    private Context mContext;
    private UUID mUUID;

    public IFTTTLight(Context context, UUID id) {
        init(context, id);
    }

    public IFTTTLight(Context context, UUID id, JSONObject config) throws JSONException{
        //Initialise dynamically from a JSON Object
        init(context, id);
    }

    private void init(Context context, UUID id){
        mCurrentState = false; //Is there a good way to get this?
        mContext = context;
        mUUID = id;

        //Setup IFTTT for web requests
        mIFTTT = IFTTT.getInstance(context);
    }

    @Override
    public void setLightColor(Integer color) throws ActionNotSupported {
        if (mName != null && mCurrentState) {
            List<String> params = new ArrayList<>();
            params.add(mName);
            params.add("#" + Integer.toHexString(color));

            mIFTTT.webhook(EVENT_PREFIX + "color", params);
        }
    }

    @Override
    public int getLightColor() throws ActionNotSupported {
        Log.e(TAG, "IFTTTLight does not know light state");
        throw new ActionNotSupported();
    }

    @Override
    public boolean setBrightness(Integer brightness) throws ActionNotSupported {
        return false;
    }

    @Override
    public int getBrightness() throws ActionNotSupported {
        return 0;
    }

    @Override
    public boolean enable() {
        if (mName != null && !mCurrentState) {
            mCurrentState = true;

            List<String> params = new ArrayList<>();
            params.add(mName);
            mIFTTT.webhook(EVENT_PREFIX + "on", params);

            return true;
        }

        return false;
    }

    @Override
    public boolean disable() {
        if (mName != null && mCurrentState) {
            mCurrentState = false;

            List<String> params = new ArrayList<>();
            params.add(mName);

            mIFTTT.webhook(EVENT_PREFIX + "off", params);

            return true;
        }

        return false;
    }

    @Override
    public boolean isEnabled() {
        return mCurrentState;
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
        //Toggle the light on and off.
        Log.d(TAG, "IFTTT quick action");
        return isEnabled() ? disable() : enable();
    }

    @Override
    public boolean extendedAction() {
        Log.d(TAG, "IFTTT extended action");
        //More in-depth lighting controls

        Intent lightControls = new Intent(mContext, LightControlPanelActivity.class);
        lightControls.putExtra(LightControlPanelActivity.ID_EXTRA, mUUID);
        mContext.startActivity(lightControls);

        return true;
    }

    @Override
    public boolean isConnected() {
        //TODO: implement this
        return true;
    }
}

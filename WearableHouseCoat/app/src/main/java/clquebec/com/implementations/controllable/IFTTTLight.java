package clquebec.com.implementations.controllable;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import clquebec.com.framework.IFTTT;
import clquebec.com.framework.controllable.ActionNotSupported;
import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllableDeviceType;
import clquebec.com.framework.controllable.ControllableLightDevice;
import clquebec.com.framework.location.Place;
import clquebec.com.wearablehousecoat.LightControlPanelActivity;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class IFTTTLight implements ControllableLightDevice {
    private static final String EVENT_PREFIX = "light_";

    private String mLocation;
    private String mName = null;
    private boolean mCurrentState;
    private IFTTT mIFTTT;
    private Context mContext;

    public IFTTTLight(Context context, String locationName) {
        init(context, locationName);
    }

    public IFTTTLight(Context context, JSONObject config) throws JSONException{
        //Initialise dynamically from a JSON Object
        String location = config.getString("location");
        init(context, location);

        setName(config.getString("name"));
    }

    private void init(Context context, String locationName){
        mLocation = locationName;
        mCurrentState = false; //Is there a good way to get this?
        mContext = context;

        //Setup IFTTT for web requests
        mIFTTT = IFTTT.getInstance(context);
    }

    @Override
    public void setLightColor(int color) throws ActionNotSupported {
        if (mName != null && mCurrentState) {
            List<String> params = new ArrayList<>();
            params.add(mName);
            params.add(mLocation);
            params.add("#" + Integer.toHexString(color));

            mIFTTT.webhook(EVENT_PREFIX + "color", params);
        }
    }

    @Override
    public boolean enable() {
        if (mName != null && !mCurrentState) {
            mCurrentState = true;

            List<String> params = new ArrayList<>();
            params.add(mName);
            params.add(mLocation);
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
            params.add(mLocation);

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
    public boolean quickAction() {
        //Toggle the light on and off.
        Log.d("IFTTTLight", "IFTTT quick action");
        return isEnabled() ? disable() : enable();
    }

    @Override
    public boolean extendedAction() {
        Log.d("IFTTTLight", "IFTTT extended action");
        //More in-depth lighting controls

        Intent lightControls = new Intent(mContext, LightControlPanelActivity.class);
        mContext.startActivity(lightControls);

        return true;
    }
}

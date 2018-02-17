package clquebec.com.implementations.controllable;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import clquebec.com.framework.IFTTT;
import clquebec.com.framework.controllable.ActionNotSupported;
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

    private Place mLocation;
    private String mName = null;
    private boolean mCurrentState;
    private IFTTT mIFTTT;
    private Context mContext;

    public IFTTTLight(Context context, Place location){
        mLocation = location;
        mCurrentState = false; //Is there a good way to get this?
        mContext = context;

        //Setup IFTTT for webrequests
        mIFTTT = IFTTT.getInstance(context);
    }

    @Override
    public void setLightColor(int color) throws ActionNotSupported {
        if(mName != null && mCurrentState){
            List<String> params = new ArrayList<>();
            params.add(mName);
            params.add(mLocation.getName());
            params.add("#"+Integer.toHexString(color));

            mIFTTT.webhook(EVENT_PREFIX+"color", params);
        }
    }

    @Override
    public boolean enable() {
        if(mName != null && !mCurrentState) {
            mCurrentState = true;

            List<String> params = new ArrayList<>();
            params.add(mName);
            params.add(mLocation.getName());
            mIFTTT.webhook(EVENT_PREFIX+"on", params);

            return true;
        }

        return false;
    }

    @Override
    public boolean disable() {
        if(mName != null && mCurrentState){
            mCurrentState = false;

            List<String> params = new ArrayList<>();
            params.add(mName);
            params.add(mLocation.getName());

            mIFTTT.webhook(EVENT_PREFIX+"off", params);

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

        /*
        try {
            setLightColor(Color.parseColor(randColor));
        }catch(ActionNotSupported e){
            Log.d("IFTTTLight", "You are using an IFTTT light that doesn't support color.");
            return false;
        }
        */

        return true;
    }
}

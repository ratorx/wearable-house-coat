package clquebec.com.implementations.controllable;

import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import clquebec.com.framework.IFTTT;
import clquebec.com.framework.controllable.ActionNotSupported;
import clquebec.com.framework.controllable.ControllableLightDevice;
import clquebec.com.framework.location.Place;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class IFTTTLight implements ControllableLightDevice {
    private static final String DEBUG_KEY = "71VEJXsAdou0wyCxcBR0D";
    private static final String EVENT_PREFIX = "light_";

    private Place mLocation;
    private String mName = null;
    private boolean mCurrentState;
    private IFTTT mIFTTT;

    public IFTTTLight(Context context, Place location){
        mLocation = location;
        mCurrentState = false; //Is there a good way to get this?

        //Setup IFTTT for webrequests
        mIFTTT = new IFTTT(context, DEBUG_KEY);
    }

    @Override
    public boolean isLightOn() {
        return mCurrentState;
    }

    @Override
    public void setLightColor(Color c) throws ActionNotSupported {
        if(mName != null && mCurrentState){
            List<String> params = new ArrayList<>();
            params.add(mName);
            params.add(mLocation.getName());
            params.add(c.toString());

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
    public void setName(String name) {
        mName = name;
    }

    @Override
    public String getName() {
        return mName;
    }
}

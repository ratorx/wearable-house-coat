package clquebec.com.framework.location;

import android.support.annotation.Nullable;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public abstract class IndoorLocationProvider {
    protected LocationChangeListener mListener;
    protected Place mLocation = null;

    public @Nullable Place getCurrentLocation(){
        return mLocation;
    }

    public void setLocationChangeListener(LocationChangeListener listener){
        mListener = listener;
        changeLocation(getCurrentLocation());
    }

    protected void changeLocation(Place currentLocation) {
        if(!mLocation.equals(currentLocation)){
            callListener(mLocation, currentLocation);
            mLocation = currentLocation;
        }
    }

    protected void callListener(Place oldLocation, Place newLocation){
        if(mListener != null){
            mListener.onLocationChanged(oldLocation, newLocation);
        }
    }
}

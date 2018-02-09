package clquebec.com.framework.people;

import android.support.annotation.Nullable;

import java.util.UUID;

import clquebec.com.framework.location.LocationChangeListener;
import clquebec.com.framework.location.Place;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 04/02/18
 */

public class Person {
    private String mName;
    private UUID mUUID;
    private Place mLocation;
    private LocationChangeListener mListener;

    public Person(UUID id){
        //TODO: Read information in from somewhere.
        mUUID = id;
        mName = "Test Person";
    }

    public String getName(){
        return mName;
    }

    public boolean equals(Object other){
        return other instanceof Person && ((Person) other).getName().equals(this.getName());
    }

    public @Nullable Place getLocation(){
        return mLocation;
    }

    public void setLocation(Place newLocation){
        if(mLocation == null || !mLocation.equals(newLocation)) {
            if (mListener != null) {
                mListener.onLocationChanged(this, mLocation, newLocation);
                mLocation = newLocation;
            }
        }
    }

    public void setLocationListener(LocationChangeListener listener){
        mListener = listener;
    }
}

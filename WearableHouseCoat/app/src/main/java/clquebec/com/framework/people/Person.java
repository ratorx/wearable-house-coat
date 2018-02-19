package clquebec.com.framework.people;

import android.support.annotation.Nullable;

import java.util.Objects;
import java.util.UUID;

import clquebec.com.framework.location.LocationChangeListener;
import clquebec.com.framework.location.Place;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 04/02/18
 */

public class Person {
    private final String mName;
    private final UUID mUUID;
    private Place mLocation;
    private LocationChangeListener mListener;

    public Person(UUID id) {
        //TODO: Read information in from somewhere.
        mUUID = id;
        mName = "Test Person";
    }

    public Person(String name) {
        //TODO: Read information in from somewhere.
        mUUID = UUID.randomUUID();
        mName = name;
    }

    public String getName() {
        return mName;
    }
    public UUID getUUID(){ return mUUID; }

    @Override
    public final boolean equals(Object other) {
        return other instanceof Person
                && Objects.equals(((Person) other).getUUID(), this.getUUID())
                && Objects.equals(((Person) other).getName(), this.getName());
    }

    @Override
    public final int hashCode(){
        //A simple hashcode.
        return Objects.hashCode(mUUID) + Objects.hashCode(mName);
    }

    public @Nullable
    Place getLocation() {
        return mLocation;
    }

    public void setLocation(Place newLocation) {
        Place oldLocation = mLocation;
        mLocation = newLocation;

        if (!Objects.equals(oldLocation, newLocation)) {
            if (mListener != null) {
                mListener.onLocationChanged(this, oldLocation, newLocation);
            }
        }
    }

    public void setLocationListener(LocationChangeListener listener) {
        mListener = listener;
    }
}

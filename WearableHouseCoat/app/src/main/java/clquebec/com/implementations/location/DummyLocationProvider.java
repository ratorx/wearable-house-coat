package clquebec.com.implementations.location;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import clquebec.com.framework.location.LocationProvider;
import clquebec.com.framework.location.LocationChangeListener;
import clquebec.com.framework.location.Place;
import clquebec.com.framework.location.Room;
import clquebec.com.framework.people.Person;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 04/02/18
 */

public class DummyLocationProvider implements LocationProvider {
    private Map<Person, Place> mLocationMap;
    private LocationChangeListener mListener;

    public DummyLocationProvider(Context context){
        mLocationMap = new HashMap<>();
        mLocationMap.put(new Person(UUID.randomUUID()), new Room(context, "Test Room"));
    }

    @Nullable
    @Override
    public Place getCurrentLocation(Person p) {
        return mLocationMap.get(p);
    }

    @Override
    public void setLocationChangeListener(@Nullable LocationChangeListener listener) {
        mListener = listener;

        //Call once for every user
        if(listener != null) {
            for (Person p : mLocationMap.keySet()) {
                listener.onLocationChanged(p, null, mLocationMap.get(p));
            }
        }
    }

    @Override
    public void forceLocationRefresh() {
        //Do nothing
    }
}

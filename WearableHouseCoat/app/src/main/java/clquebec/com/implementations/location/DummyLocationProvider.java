package clquebec.com.implementations.location;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import clquebec.com.framework.location.LocationChangeListener;
import clquebec.com.framework.location.LocationGetter;
import clquebec.com.framework.location.Place;
import clquebec.com.framework.location.Room;
import clquebec.com.framework.people.Person;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 04/02/18
 */

public class DummyLocationProvider implements LocationGetter {
    private Map<Person, Place> mLocationMap;
    @SuppressWarnings("FieldCanBeLocal")
    private LocationChangeListener mListener;

    public DummyLocationProvider(Context context) {
        mLocationMap = new HashMap<>();
        mLocationMap.put(Person.getPerson(UUID.randomUUID()), new Room(context, "Test Room"));
    }

    @Nullable
    @Override
    public Place getLocation(Person p) {
        return mLocationMap.get(p);
    }

    @Override
    public void refreshLocations() {
        //Do nothing
    }
}

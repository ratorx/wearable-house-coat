package com.clquebec.framework.people;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.clquebec.framework.location.LocationChangeListener;
import com.clquebec.framework.location.Place;
import com.clquebec.framework.storage.ConfigurationStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 04/02/18
 */

public class Person {
    private static final String TAG = "Person";
    private static final Map<UUID, Person> people = new HashMap<>();

    private static ConfigurationStore mConfigStore;

    private String mName;
    private String mEmail;
    private final UUID mUUID;
    private Place mLocation;
    private LocationChangeListener mListener;

    //Used for production
    public static Person getPerson(Context c, UUID id) {
        if(mConfigStore == null) {
            return getPerson(ConfigurationStore.getInstance(c), id);
        }else {
            return getPerson(mConfigStore, id);
        }
    }

    //Mostly used for testing
    public static Person getPerson(ConfigurationStore config, UUID id){
        if (people.containsKey(id)) {
            return people.get(id);
        }

        //Update mConfigStore
        mConfigStore = config;

        Person p = new Person(id);
        people.put(id, p);
        return p;
    }

    private Person(UUID id) {
        mUUID = id;
        mName = "Unnamed";

        //Load person information from configuration store
        mConfigStore.onConfigAvailable(config -> {
            JSONObject personData = config.getPersonInformation(id);

            try{
                mName = personData.getString("name");
            }catch(JSONException | NullPointerException e){
                Log.e(TAG, "Could not get person name for UID "+mUUID.toString());
            }

            try{
                mEmail = personData.getString("email");
            }catch(JSONException | NullPointerException e){
                Log.e(TAG, "Could not get person email for UID "+mUUID.toString());
            }
        });
    }

    public String getName() {
        return mName;
    }
    public UUID getUUID(){ return mUUID; }
    public String getEmail(){ return mEmail; }

    @Override
    public final boolean equals(Object other) {
        return other instanceof Person
                && Objects.equals(((Person) other).getUUID(), this.getUUID());
    }

    @Override
    public final int hashCode(){
        //A simple hashcode.
        return Objects.hashCode(mUUID);
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

        if(listener != null && mLocation != null) {
            listener.onLocationChanged(this, null, mLocation);
        }
    }
}

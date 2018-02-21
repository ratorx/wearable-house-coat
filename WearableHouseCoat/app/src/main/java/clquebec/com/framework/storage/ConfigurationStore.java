package clquebec.com.framework.storage;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import clquebec.com.environment.Keys;
import clquebec.com.framework.HTTPRequestQueue;
import clquebec.com.framework.location.Building;
import clquebec.com.framework.location.Room;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 15/02/18
 */

public class ConfigurationStore {
    private static final String TAG = "ConfigurationStore";
    public static final String CONFIG_SERVER = "https://shell.srcf.net:3000/";
    private static ConfigurationStore mInstance;

    private HTTPRequestQueue mQueue;
    private JSONObject mData;
    private Set<ConfigurationAvailableCallback> mCallbacks;
    private Map<UUID, JSONObject> mPersonDataMap;

    public interface ConfigurationAvailableCallback{
        void onConfigurationAvailable(ConfigurationStore config);
    }

    //Used for production
    private ConfigurationStore(Context c){
        this(c, HTTPRequestQueue.getRequestQueue(c));
    }

    //This constructor is mostly used for testing
    private ConfigurationStore(Context c, HTTPRequestQueue queue) {
        mQueue = queue;

        mCallbacks = new HashSet<>();
        mPersonDataMap = new HashMap<>();

        Log.d(TAG, "Requesting config store");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, CONFIG_SERVER, null,
                this::setData,
                error -> {
                    try {
                        setData(new JSONObject(Keys.ConfigJSON));
                    }catch(JSONException e){
                        Log.e(TAG, "Error creating default config, "+e.getMessage());
                    }
                }
        );

        mQueue.addToRequestQueue(request);

        //Required for testing - so that the singleton can be instantiated
        if(mInstance == null){
            mInstance = this;
        }
    }

    public void setData(JSONObject data) {
        this.mData = data;

        if(data != null) {
            Log.d(TAG, "Received config store");
            Log.d(TAG, data.toString());

            //Load in People as a UUID->JSONObject map
            try {
                JSONArray people = mData.getJSONArray("people");
                for (int i = 0; i < people.length(); i++) {
                    try {
                        JSONObject personData = people.getJSONObject(i);
                        UUID id = new UUID(0, personData.getLong("id"));

                        mPersonDataMap.put(id, personData);
                    } catch (JSONException e) {
                        Log.e(TAG, "Unable to parse Person " + i + ": " + e.getMessage());
                    }
                }
            } catch (JSONException | NullPointerException e) {
                Log.e(TAG, "No 'people' array: there will not be any users.");
            }

            //Call all the callbacks, and remove them so they're only called once.
            //Not thread safe ("synchronised" would help).
            Set<ConfigurationAvailableCallback> callbacks = new HashSet<>(mCallbacks);
            for (ConfigurationAvailableCallback callback : callbacks) {
                callback.onConfigurationAvailable(this);
                mCallbacks.remove(callback);
            }
        }
    }

    public void onConfigAvailable(ConfigurationAvailableCallback callback){
        Log.d(TAG, "onConfigAvailable added");
        if(mData != null) {
            callback.onConfigurationAvailable(this);
        }else{
            mCallbacks.add(callback);
        }
    }

    //Asynchronous singleton design
    public static ConfigurationStore getInstance(Context c){
        if(mInstance == null){
            mInstance = new ConfigurationStore(c);
        }

        return mInstance;
    }

    //Get a building with all the rooms
    public Building getBuilding(Context c){
        Set<Room> roomSet = new HashSet<>();

        try {
            JSONArray rooms = mData.getJSONArray("rooms");
            for(int i = 0; i < rooms.length(); i++){
                try {
                    JSONObject roomData = rooms.getJSONObject(i);

                    //Instantiate Room with JSONData
                    Room room = new Room(c, roomData);
                    roomSet.add(room);
                }catch(JSONException e){
                    Log.e(TAG, "Could not instantiate Room "+i+": "+e.getMessage());
                }
            }

            return new Building(c, "My Building", roomSet);
        }catch(JSONException e){
            Log.e(TAG, "Could not create building, "+e.getMessage());
        }

        return new Building(c, "My Building");
    }

    //Gets the JSON information about a person
    public JSONObject getPersonInformation(UUID id){
        try {
            //Perform a copy
            return new JSONObject(mPersonDataMap.get(id).toString());
        }catch(JSONException | NullPointerException e){
            //Person does not exist
            return null;
        }
    }
}

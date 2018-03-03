package com.clquebec.framework.storage;

import android.content.Context;
import android.content.Intent;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.clquebec.environment.Keys;
import com.clquebec.framework.HTTPRequestQueue;
import com.clquebec.framework.controllable.ControllableDevice;
import com.clquebec.framework.location.Building;
import com.clquebec.framework.location.Room;
import com.clquebec.framework.people.Person;
import com.clquebec.wearablehousecoat.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 15/02/18
 */

public class ConfigurationStore {
    private static final String TAG = "ConfigurationStore";
    public static final String CONFIG_SERVER = "http://shell.srcf.net:8003/";
    private static ConfigurationStore mInstance;

    private HTTPRequestQueue mQueue;
    private JSONObject mData;
    private Set<ConfigurationAvailableCallback> mCallbacks;
    private Map<UUID, JSONObject> mPersonDataMap;
    private Map<UUID, ControllableDevice> mDeviceMap;

    private String mUserEmail;
    private String mFBInstanceId;
    private UUID mUUID;

    public interface ConfigurationAvailableCallback{
        void onConfigurationAvailable(ConfigurationStore config);
    }

    //Used for production
    private ConfigurationStore(Context c){
        this(c, HTTPRequestQueue.getRequestQueue(c));

        GoogleSignInAccount mAccount = GoogleSignIn.getLastSignedInAccount(c);
        if(mAccount != null) {
            mUserEmail = mAccount.getEmail();
        }
    }

    //This constructor is mostly used for testing
    private ConfigurationStore(Context c, HTTPRequestQueue queue) {
        mQueue = queue;

        mCallbacks = new HashSet<>();
        mPersonDataMap = new HashMap<>();
        mDeviceMap = new HashMap<>();

        tryGetConfigFromServer(c);

        //Required for testing - so that the singleton can be instantiated
        if(mInstance == null){
            mInstance = this;
        }
    }

    public void tryGetConfigFromServer(Context c){
        String url = getServer() + "config";

        Log.d(TAG, "Requesting config store: "+url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> setData(c, response),
                error -> {
                    try {
                        setData(c, new JSONObject(Keys.ConfigJSON));
                    }catch(JSONException e){
                        Log.e(TAG, "Error creating default config, "+e.getMessage());

                        //Show confirmation
                        Intent intent = new Intent(c, ConfirmationActivity.class);
                        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
                        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, c.getString(R.string.msg_config_unavailable));
                        c.startActivity(intent);
                    }
                }
        );

        mQueue.addToRequestQueue(request);
    }

    public void setData(Context context, JSONObject data) {
        if(data != null) {
            Log.d(TAG, "Received config store");
            Log.d(TAG, data.toString());

            try {
                this.mData = data.getJSONObject("data");
            }catch(JSONException e){
                this.mData = null;
                return;
            }

            //Load in People as a UUID->JSONObject map
            try {
                JSONArray people = mData.getJSONArray("people");
                for (int i = 0; i < people.length(); i++) {
                    try {
                        JSONObject personData = people.getJSONObject(i);
                        UUID id = UUID.fromString(personData.getString("uid"));

                        if(Objects.equals(mUserEmail, personData.getString("email"))){
                            //Set my UUID to this one
                            mUUID = id;
                        }

                        mPersonDataMap.put(id, personData);
                    } catch (JSONException e) {
                        Log.e(TAG, "Unable to parse Person " + i + ": " + e.getMessage());
                    }
                }
            } catch (JSONException | NullPointerException e) {
                Log.e(TAG, "No 'people' array: there will not be any users.");
            }

            //Load in Devices as a UUID->ControllableDevice map
            try {
                JSONArray devices = mData.getJSONArray("devices");
                for (int i = 0; i < devices.length(); i++) {
                    try {
                        JSONObject deviceData = devices.getJSONObject(i);
                        UUID id = UUID.fromString(deviceData.getString("uid"));

                        //Get device config
                        JSONObject deviceConfig = deviceData.getJSONObject("config");

                        //Load Device in dynamically!
                        try {
                            Class<?> deviceClass = Class.forName("com.clquebec.implementations.controllable." + deviceData.getString("type"));
                            Constructor<?> getInstance = deviceClass.getConstructor(Context.class, UUID.class, JSONObject.class);
                            ControllableDevice device = (ControllableDevice) getInstance.newInstance(context, id, deviceConfig);

                            //Set device name
                            device.setName(deviceData.getString("name"));

                            //Put device into map
                            mDeviceMap.put(id, device);
                        }catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
                            Log.e(TAG, "Could not instantiate device "+deviceData.getString("type")+": "+e.getMessage());
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Unable to parse JSON for Device " + i + ": " + e.getMessage());
                    }
                }
            } catch (JSONException | NullPointerException e) {
                Log.e(TAG, "No 'devices' array: there will not be any users.");
            }

            //Call all the callbacks, and remove them so they're only called once.
            //Not thread safe ("synchronised" would help).
            Set<ConfigurationAvailableCallback> callbacks = new HashSet<>(mCallbacks);
            for (ConfigurationAvailableCallback callback : callbacks) {
                callback.onConfigurationAvailable(this);
                mCallbacks.remove(callback);
            }
        }else{
            mData = null;
        }
    }

    public void onConfigAvailable(ConfigurationAvailableCallback callback){
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

    public ControllableDevice getDevice(UUID id){
        try {
            //Perform a copy
            return mDeviceMap.get(id);
        }catch(NullPointerException e){
            //Device does not exist
            return null;
        }
    }

    public UUID getMyUUID(){
        if(mUUID == null){
            return new UUID(0, 0);
        }else{
            return mUUID;
        }
    }

    public String getMyEmail(){
        return mUserEmail;
    }

    public void setMyEmail(String email){
        mUserEmail = email;

        if(mData != null){
            //Try and find "me"
            for(UUID personID : mPersonDataMap.keySet()){
                Person p = Person.getPerson(this, personID);
                if(Objects.equals(mUserEmail, p.getEmail())){
                    mUUID = personID;
                    tryAndSendFBIdToServer();
                }
            }
        }
    }

    private void tryAndSendFBIdToServer(){
        if(mUUID != null && mFBInstanceId != null){
            Log.d(TAG, "Sending FBID to server with ID: "+mUUID+" FBid: "+mFBInstanceId);
            String url = getServer() + "adduser?fbid="+mFBInstanceId+"&user="+mUUID.toString();
            Log.d(TAG, url);
            mQueue.addToRequestQueue(new StringRequest(Request.Method.GET, url,
                    response -> Log.d(TAG, response),
                    error -> {
                        Log.e(TAG, "Error sending instance ID to server, trying again");
                        tryAndSendFBIdToServer();
                    }
            ));
        }
    }

    public String getServer(){
        try{
            return mData.getString("server");
        }catch(Exception e){
            return CONFIG_SERVER;
        }
    }

    public void setMyInstanceId(String instance){
        mFBInstanceId = instance;
        tryAndSendFBIdToServer();
    }
}

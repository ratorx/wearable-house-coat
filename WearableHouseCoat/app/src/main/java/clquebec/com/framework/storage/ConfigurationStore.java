package clquebec.com.framework.storage;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import clquebec.com.environment.Keys;
import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.location.Building;
import clquebec.com.framework.location.Room;
import clquebec.com.implementations.location.FINDLocationProvider;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 15/02/18
 */

public class ConfigurationStore {
    public static final String CONFIG_SERVER = "https://shell.srcf.net:3000/";
    private static ConfigurationStore mInstance;

    private RequestQueue mQueue;
    private Context mContext;
    private JSONObject mData;

    private ConfigurationStore(Context c) {
        mContext = c;
        mQueue = Volley.newRequestQueue(c);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, CONFIG_SERVER, null,
                response -> mData = response,
                error -> {
                    //Make a default mData
                    //TODO: not this?
                    try {
                        mData = new JSONObject(Keys.ConfigJSON);
                    }catch(JSONException e){
                        Log.e("ConfigurationStore", "Error creating default config, "+e.getMessage());
                    }
                }
        );
    }

    //Singleton design pattern
    public static ConfigurationStore getInstance(Context c){
        if(mInstance == null){
            mInstance = new ConfigurationStore(c);
        }

        return mInstance;
    }

    //Get a building with all the rooms
    public Building getBuilding(){
        Set<Room> roomSet = new HashSet<>();

        try {
            JSONArray rooms = mData.getJSONArray("rooms");
            for(int i = 0; i < rooms.length(); i++){
                JSONObject roomData = rooms.getJSONObject(i);

                //Instantiate Room with JSONData
                Room room = new Room(mContext, roomData);
                roomSet.add(room);

            }

        }catch(JSONException e){
            Log.e("ConfigurationStore", "Could not create building "+e.getMessage());
        }

        return new Building(mContext, "My Building", roomSet);
    }
}

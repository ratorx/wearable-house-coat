package clquebec.com.framework.location;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllableDeviceType;
import clquebec.com.framework.storage.ConfigurationStore;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class Room extends Place {
    private final static String TAG = "Room";
    private String mName;
    private List<ControllableDevice> mDevices;

    //Used for production
    public Room(Context context, JSONObject roomData) throws JSONException {
        this(ConfigurationStore.getInstance(context), roomData);
    }

    //Used for testing
    public Room(ConfigurationStore configStore, JSONObject roomData) throws JSONException{
        //Only use LSB - for now
        super(new UUID(0L, roomData.getLong("uid")));

        mName = roomData.getString("name");

        //Instantiate devices
        mDevices = new ArrayList<>();

        if(roomData.has("devices")) {
            JSONArray deviceList = roomData.getJSONArray("devices");

            Log.d(TAG, deviceList.toString());
            configStore.onConfigAvailable(config -> {
                for (int i = 0; i < deviceList.length(); i++) {
                    try {
                        //Get device ID
                        UUID deviceID = new UUID(0, deviceList.getLong(i));

                        //Add device to list
                        ControllableDevice device = config.getDevice(deviceID);
                        if(device != null) {
                            mDevices.add(device);
                        }
                    }catch(JSONException e){
                        Log.e(TAG, "Could not parse JSON for Device "+i);
                    }
                }
            });
        }
    }

    public Room(Context context, String name) {
        super(UUID.randomUUID());
        mName = name;
        mDevices = new ArrayList<>();
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public ControllableDeviceType getType() {
        return null;
    }

    @Override
    public UUID getID() {
        return mUUID;
    }

    @Override
    public List<ControllableDevice> getDevices() {
        return new ArrayList<>(mDevices);
    }

}
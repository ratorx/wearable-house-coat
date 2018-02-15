package clquebec.com.framework.location;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllableDeviceType;


/**
 * WearableHouseCoat
 * Author: Jack
 * Creation Date: 03/02/18
 */

public class Building extends Place {
    private String mName;
    private Context mContext;
    private Set<Room> mRooms;

    public Building(Context context, String name) {
        super(UUID.randomUUID());
        mName = name;
        mContext = context;
        mRooms = new HashSet<>();
    }

    public Building(Context context, String name, Set<Room> rooms) {
        super(UUID.randomUUID());
        mName = name;
        mContext = context;

        mRooms = new HashSet<>(rooms);
    }

    public void addRoom(Room newRoom) {
        // Sets don't add duplicates, so just need to make sure we don't
        mRooms.add(newRoom);
    }

    public Set<Room> getRooms() {
        return new HashSet<>(mRooms);
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public ControllableDeviceType getType() {
        return null;
    }

    @Override
    public ControllableDevice getDeviceInstance(Context context, JSONObject config) {
        try{
            String name = config.getString("name");
            return new Building(context, name);
        }catch(JSONException e){
            Log.e("Building", "JSON does not have required 'name'");
            return new Building(context, "My Building");
        }
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public List<ControllableDevice> getDevices() {
        ArrayList<ControllableDevice> devices = new ArrayList<>();
        for (Room r : mRooms) {
            devices.addAll(r.getDevices());
        }
        return devices;
    }

}



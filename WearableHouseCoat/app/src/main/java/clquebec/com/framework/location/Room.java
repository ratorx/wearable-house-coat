package clquebec.com.framework.location;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllableDeviceType;
import clquebec.com.framework.people.Person;
import clquebec.com.implementations.controllable.IFTTTLight;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class Room extends Place {
    private String mName;
    private Context mContext;
    private List<ControllableDevice> mDevices;
    private final Set<Person> mPeople = new HashSet<>();

    public Room(Context context, JSONObject roomData) throws JSONException{
        super(UUID.randomUUID());

        mContext = context;
        mName = roomData.getString("name");
        //Only use LSB - for now
        mUUID = new UUID(0L, roomData.getLong("uid"));

        //Instantiate devices
        mDevices = new ArrayList<>();
        JSONArray deviceList = roomData.getJSONArray("devices");
        for(int i = 0; i < deviceList.length(); i++){
            JSONObject deviceData = deviceList.getJSONObject(i);
            //Load in device dynamically - Java reflection!
            try {
                Class<?> deviceClass = Class.forName("clquebec.com.implementations.controllable." + deviceData.getString("name"));
                Method getInstance = deviceClass.getMethod("getDeviceInstance", Context.class, JSONObject.class);
                ControllableDevice device = (ControllableDevice) getInstance.invoke(null, mContext, deviceData);

                mDevices.add(device);
            }catch(ClassNotFoundException e){
                Log.e("Room", "Failed to create device "+deviceData.getString("name"));
            }catch(IllegalAccessException e) {
                Log.e("Room", "Do not have permissions to instantiate device " + deviceData.getString("name"));
            }catch(ClassCastException | NoSuchMethodException | InvocationTargetException e){
                Log.e("Room", "Class is not a controllable device "+deviceData.getString("name"));
            }
        }

        //TODO: Read this from somewhere - do we really need this??
        UUID personId = UUID.randomUUID();
        Person myPerson = new Person(personId);

        mPeople.add(myPerson);
    }

    public Room(Context context, String name) {
        super(UUID.randomUUID());
        mName = name;
        mContext = context;

        //TODO: Read this from somewhere - do we really need this??
        UUID personId = UUID.randomUUID();
        Person myPerson = new Person(personId);

        mPeople.add(myPerson);
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
    public ControllableDevice getDeviceInstance(Context context, JSONObject config) {
        try {
            return new Room(context, config);
        }catch(JSONException e){
            Log.e("Room", "Could not instantiate based on JSON config "+e.getMessage());
            throw new IllegalArgumentException("Malformed JSON for Room instantiation");
        }
    }

    @Override
    public UUID getID() {
        return mUUID;
    }

    @Override
    public List<ControllableDevice> getDevices() {
        //TODO: Read this from somewhere
        //For now, return a set with just an IFTTT Light controller.
        List<ControllableDevice> devices = new ArrayList<>();

        ControllableDevice myLight = new IFTTTLight(mContext, this);
        myLight.setName("IFTTT Test");

        devices.add(myLight);

        return devices;
    }

    @Override
    public Set<Person> getPeople() {
        return new HashSet<>(mPeople);
    }
}

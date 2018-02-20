package clquebec.com.framework.location;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
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
        //Only use LSB - for now
        super(new UUID(0L, roomData.getLong("id")));

        mContext = context;
        mName = roomData.getString("name");

        //Instantiate devices
        mDevices = new ArrayList<>();

        if(roomData.has("devices")) {
            JSONArray deviceList = roomData.getJSONArray("devices");

            Log.d("Room", deviceList.toString());
            for (int i = 0; i < deviceList.length(); i++) {
                JSONObject deviceData = deviceList.getJSONObject(i);
                //Load in device dynamically - Java reflection!
                try {
                    //Get device config
                    JSONObject deviceConfig = deviceData.getJSONObject("config");
                    deviceConfig.put("location", mName);

                    //Load class and call getDeviceInstance
                    Class<?> deviceClass = Class.forName("clquebec.com.implementations.controllable." + deviceData.getString("type"));
                    Constructor<?> getInstance = deviceClass.getConstructor(Context.class, JSONObject.class);
                    ControllableDevice device = (ControllableDevice) getInstance.newInstance(mContext, deviceConfig);

                    mDevices.add(device);
                } catch (ClassNotFoundException e) {
                    Log.e("Room", "Failed to create device " + deviceData.getString("type"));
                } catch (IllegalAccessException e) {
                    Log.e("Room", "Do not have permissions to instantiate device " + deviceData.getString("type"));
                } catch (ClassCastException | NoSuchMethodException | InvocationTargetException e) {
                    Log.e("Room", "Class is not a controllable device " + deviceData.getString("type"));
                } catch (InstantiationException e) {
                    Log.e("Room", "Class does not have a valid (Context, JSONObject) constructor");
                }
            }
        }
    }

    public Room(Context context, String name) {
        super(UUID.randomUUID());
        mName = name;
        mContext = context;
        mDevices = new ArrayList<>();

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
    public UUID getID() {
        return mUUID;
    }

    @Override
    public List<ControllableDevice> getDevices() {
        return new ArrayList<>(mDevices);
    }

    @Override
    public Set<Person> getPeople() {
        return new HashSet<>(mPeople);
    }
}
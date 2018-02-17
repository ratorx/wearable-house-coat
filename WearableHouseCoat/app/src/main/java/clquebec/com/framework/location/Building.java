package clquebec.com.framework.location;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllableDeviceType;
import clquebec.com.framework.people.Person;


/**
 * WearableHouseCoat
 * Author: Jack
 * Creation Date: 03/02/18
 */

public class Building extends Place {
    private String mName;
    private UUID mUUID;
    private Context mContext;
    private Set<Room> mRooms;

    public Building(Context context, String name) {
        mName = name;
        mUUID = UUID.randomUUID();
        mContext = context;
        mRooms = new HashSet<>();
    }

    public Building(Context context, String name, Set<Room> rooms) {
        mName = name;
        mUUID = UUID.randomUUID();
        mContext = context;
        mRooms = rooms;
    }

    public void addRoom(Room newRoom) {
        // Sets don't add duplicates, so just need to make sure we don't
        mRooms.add(newRoom);
    }

    public Set<Room> getRooms() {
        // Currently doesn't clone - so be careful when removing from it!
        return mRooms;
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
    public void setName(String name) {
        mName = name;
    }

    @Override
    public UUID getID() {
        return mUUID;
    }

    @Override
    public List<ControllableDevice> getDevices() {
        ArrayList<ControllableDevice> devices = new ArrayList<>();
        for (Room r : mRooms) {
            devices.addAll(r.getDevices());
        }
        return devices;
    }

    @Override
    public Set<Person> getPeople() {
        HashSet<Person> people = new HashSet<>();
        for (Room r : mRooms) {
            people.addAll(r.getPeople());
        }
        return people;
    }

}



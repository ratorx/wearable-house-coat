package com.clquebec.framework.location;

import android.content.Context;

import com.clquebec.framework.controllable.ControllableDevice;
import com.clquebec.framework.controllable.ControllableDeviceType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


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

    @Override
    //TODO: WTF???
    public boolean isConnected(){
        return true;
    }

}



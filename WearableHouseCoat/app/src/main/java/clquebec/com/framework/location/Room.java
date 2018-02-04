package clquebec.com.framework.location;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllableLightDevice;
import clquebec.com.implementations.controllable.IFTTTLight;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class Room extends Place {
    private String mName;
    private UUID mUUID;
    private Context mContext;

    public Room(Context context, String name){
        mName = name;
        mUUID = UUID.randomUUID();
        mContext = context;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public UUID getID() {
        return mUUID;
    }

    @Override
    public Set<ControllableDevice> getDevices() {
        //TODO: Read this from somewhere
        //For now, return a set with just an IFTTT Light controller.
        Set<ControllableDevice> devices = new HashSet<>();

        ControllableDevice myLight = new IFTTTLight(mContext, this);
        myLight.setName("IFTTT Test");

        devices.add(myLight);

        return devices;
    }
}

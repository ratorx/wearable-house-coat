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
        //TODO: Read this from somewhere

        Set<Person> people = new HashSet<>();

        UUID personId = UUID.randomUUID();
        Person myPerson = new Person(personId);

        people.add(myPerson);
        return people;
    }
}

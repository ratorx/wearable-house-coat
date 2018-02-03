package clquebec.com.framework.location;

import java.util.UUID;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class Room extends Place {
    private String mName;
    private UUID mUUID;

    public Room(String name){
        mName = name;
        mUUID = UUID.randomUUID();
    }
    @Override
    public String getName() {
        return mName;
    }

    @Override
    public UUID getID() {
        return mUUID;
    }
}

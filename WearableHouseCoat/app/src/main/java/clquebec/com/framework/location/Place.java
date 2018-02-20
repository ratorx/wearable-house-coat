package clquebec.com.framework.location;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllableDeviceGroup;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public abstract class Place implements ControllableDeviceGroup {
    @SuppressWarnings("WeakerAccess")
    //We want this to be protected, even if extended into a different package.
    protected final UUID mUUID;

    public Place(UUID id){
        mUUID = id;
    }

    public UUID getID(){
        return mUUID;
    }

    public abstract List<ControllableDevice> getDevices();

    public boolean equals(Object other) {
        return other instanceof Place && Objects.equals(((Place) other).getID(), this.getID());
    }

    public int hashCode(){
        return Objects.hashCode(getID());
    }

    //Device Group methods
    @Override
    public boolean enable() {
        //Enable every device in this place. Return true if success for all.
        boolean returnValue = true;
        for (ControllableDevice d : getDevices()) {
            returnValue = returnValue && d.enable();
        }
        return returnValue;
    }

    @Override
    public boolean disable() {
        //Disable every device in this place. Return true if success for all.
        boolean returnValue = true;
        for (ControllableDevice d : getDevices()) {
            returnValue = returnValue && d.disable();
        }
        return returnValue;
    }

    @Override
    public boolean isEnabled() {
        return getDevices().stream().allMatch(d -> isEnabled());
    }

    @Override
    public boolean quickAction() {
        //Toggle all on or off
        return isEnabled() ? disable() : enable();
    }

    @Override
    public boolean extendedAction() {
        //TODO: Implement something useful here, like displaying a list / sub-list of toggles.
        return false;
    }
}

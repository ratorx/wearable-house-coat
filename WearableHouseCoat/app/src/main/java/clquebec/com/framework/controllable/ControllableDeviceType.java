package clquebec.com.framework.controllable;

import clquebec.com.wearablehousecoat.R;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public enum ControllableDeviceType {
    //These need to match up with the values in attrs.xml or there will be trouble
    //As in, be the same order
    LIGHT, SOUND, THERMOSTAT, CURTAINS, OVEN;

    public static ControllableDeviceType getType(int typeNumber){
        return ControllableDeviceType.values()[typeNumber];
    }

    public int getIcon(){
        //Maps from ControllableDeviceType to drawable resources
        switch (this) {
            case LIGHT:
                return R.drawable.ic_lightbulb;
            case SOUND:
                return R.drawable.ic_speaker;
            case THERMOSTAT:
                return R.drawable.ic_thermo;
            case OVEN:
                return R.drawable.ic_oven;
            default:
                return 0;
        }
    }

    public String toString() {
        /* TODO: replace this with something proper with XML resources */
        switch (this) {
            case LIGHT:
                return "Light";
            case SOUND:
                return "Sound";
            case THERMOSTAT:
                return "Thermostat";
            case OVEN:
                return "Oven";
            case CURTAINS:
                return "Curtains";
            default:
                return "";
        }

    }
}

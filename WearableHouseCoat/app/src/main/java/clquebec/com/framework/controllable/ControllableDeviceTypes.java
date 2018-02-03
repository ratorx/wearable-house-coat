package clquebec.com.framework.controllable;

import clquebec.com.wearablehousecoat.R;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class ControllableDeviceTypes {

    //These need to match up with the values in attrs.xml or there will be trouble
    public static final int LIGHT = 0;
    public static final int SOUND = 1;
    public static final int THERMOSTAT = 2;
    public static final int CURTAINS = 3;
    public static final int OVEN = 4;

    public static int getIcon(int device){
        //Maps from ControllableDeviceTypes to drawable resources
        switch (device) {
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
}

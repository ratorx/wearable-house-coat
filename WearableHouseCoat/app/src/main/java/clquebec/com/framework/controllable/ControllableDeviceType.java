package clquebec.com.framework.controllable;

import android.content.res.Resources;

import clquebec.com.wearablehousecoat.R;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public enum ControllableDeviceType {
    //These need to match up with the values in attrs.xml or there will be trouble
    //As in, be the same order
    LIGHT, SOUND, THERMOSTAT, CURTAINS, OVEN, TOILET;

    public static ControllableDeviceType getType(int typeNumber) {
        return ControllableDeviceType.values()[typeNumber];
    }

    public int getIcon() {
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
            case CURTAINS:
                return R.drawable.ic_curtains;
            case TOILET:
                //return R.drawable.ic_toilet;
                return 0;
            default:
                return 0;
        }
    }

    public int getFadedIcon() {
        switch (this) {
            case LIGHT:
                return R.drawable.ic_lightbulb_fade;
            case SOUND:
                return R.drawable.ic_speaker_fade;
            case THERMOSTAT:
                return R.drawable.ic_thermo_fade;
            case OVEN:
                return R.drawable.ic_oven_fade;
            case CURTAINS:
                return R.drawable.ic_curtains_fade;
            case TOILET:
                //return R.drawable.ic_toilet_fade;
                return 0;
            default:
                return 0;

        }
    }

    public String toString() {
        //Returns a (translatable) string version of the device type.

        Resources r = Resources.getSystem();
        switch (this) {
            case LIGHT:
                return r.getString(R.string.light);
            case SOUND:
                return r.getString(R.string.sound);
            case THERMOSTAT:
                return r.getString(R.string.thermostat);
            case OVEN:
                return r.getString(R.string.oven);
            case CURTAINS:
                return r.getString(R.string.curtains);
            case TOILET:
                return r.getString(R.string.toilet);
            default:
                return "";
        }

    }
}

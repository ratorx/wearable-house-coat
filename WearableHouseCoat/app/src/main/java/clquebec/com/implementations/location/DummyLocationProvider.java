package clquebec.com.implementations.location;

import android.content.Context;

import clquebec.com.framework.location.IndoorLocationProvider;
import clquebec.com.framework.location.Place;
import clquebec.com.framework.location.Room;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 04/02/18
 */

public class DummyLocationProvider extends IndoorLocationProvider {

    public DummyLocationProvider(Context context){
        super.changeLocation(new Room(context, "Test Room"));
    }
}

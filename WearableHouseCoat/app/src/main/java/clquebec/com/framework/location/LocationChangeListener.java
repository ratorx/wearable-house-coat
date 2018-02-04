package clquebec.com.framework.location;

import java.util.function.Function;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 04/02/18
 */

public interface LocationChangeListener {
    void onLocationChanged(Place oldLocation, Place newLocation);
}

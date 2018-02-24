package com.clquebec.framework.location;

import com.clquebec.framework.people.Person;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 04/02/18
 */

public interface LocationChangeListener {
    void onLocationChanged(Person user, Place oldLocation, Place newLocation);
}

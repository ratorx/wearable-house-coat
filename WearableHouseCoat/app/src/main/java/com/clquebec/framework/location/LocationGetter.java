package com.clquebec.framework.location;

import android.support.annotation.Nullable;

import com.clquebec.framework.people.Person;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public interface LocationGetter {
    @Nullable
    Place getLocation(Person p);

    void refreshLocations();
}

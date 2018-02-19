import junit.framework.TestCase;

import java.util.UUID;

import clquebec.com.framework.location.LocationChangeListener;
import clquebec.com.framework.location.Place;
import clquebec.com.framework.people.Person;
import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 19/02/18
 */

public class PersonTest extends TestCase {
    private Person mPerson;
    private String mTestName = "TestName";

    public void testGetName(){
        //Instantiating with a name should yield that name
        mPerson = new Person(mTestName);
        assertThat(mPerson.getName()).isEqualTo(mTestName);
    }

    public void testGetUUID(){
        //Instantiating with a UUID should yield that UUID
        UUID uuid = UUID.randomUUID();
        mPerson = new Person(uuid);
        assertThat(mPerson.getUUID()).isEqualTo(uuid);
    }

    public void testEquality(){
        //Call EqualsVerifier
        Place place1 = mock(Place.class);
        Place place2 = mock(Place.class);
        EqualsVerifier.forClass(Person.class)
                .withPrefabValues(Place.class, place1, place2)
                .withIgnoredFields("mLocation") //Don't do equality on locations for people - it's mutable
                .withIgnoredFields("mListener") //Don't do equality on listeners - it's mutable
                .verify();
    }

    public void testGetLocation(){
        Place place1 = mock(Place.class);
        Place place2 = mock(Place.class);
        mPerson = new Person(mTestName);

        //Setting location then immediately getting it should yield that location
        mPerson.setLocation(place1);
        assertThat(mPerson.getLocation()).isEqualTo(place1);

        //Changing location should yield the new location
        mPerson.setLocation(place2);
        assertThat(mPerson.getLocation()).isEqualTo(place2);
    }

    public void testLocationListener(){
        mPerson = new Person(mTestName);
        Place place1 = mock(Place.class);
        Place place2 = mock(Place.class);

        //Calling with a null location listener should reset location listener
        mPerson.setLocationListener(null);

        //Mock listener
        LocationChangeListener listener = mock(LocationChangeListener.class);
        mPerson.setLocationListener(listener);

        //Set location - should call location listener once with mPerson, null old location, and place1
        mPerson.setLocation(place1);
        verify(listener, times(1)).onLocationChanged(mPerson, null, place1);

        //Should notify the location change once
        mPerson.setLocation(place2);
        verify(listener, times(1)).onLocationChanged(mPerson, place1, place2);

        //Should _not_ notify
        mPerson.setLocation(place2);
        verify(listener, times(0)).onLocationChanged(mPerson, place2, place2);

        //Should still notify on change to null
        mPerson.setLocation(null);
        verify(listener, times(1)).onLocationChanged(mPerson, place2, null);

        //Test with a real implementation
        mPerson.setLocationListener((person, oldLocation, newLocation) -> {
            //Person should be the target
            assertThat(person).isEqualTo(mPerson);

            //oldLocation should be null in this example
            assertThat(oldLocation).isEqualTo(null);

            //newLocation should be place1, and should be mLocation
            assertThat(newLocation).isEqualTo(place1).isEqualTo(mPerson.getLocation());
        });
        mPerson.setLocation(place1);
    }
}

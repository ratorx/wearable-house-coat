import android.content.Context;

import com.clquebec.framework.HTTPRequestQueue;
import com.clquebec.framework.location.LocationChangeListener;
import com.clquebec.framework.location.Place;
import com.clquebec.framework.people.Person;
import com.clquebec.framework.storage.ConfigurationStore;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 19/02/18
 */

@RunWith(MockitoJUnitRunner.class)
public class PersonTest extends TestCase {
    private Person mPerson;

    @Mock(name="c")
    private Context mContext;

    //For testing instantiation from configuration store
    @Mock(name="queue")
    private HTTPRequestQueue requestQueue;

    @InjectMocks
    private ConfigurationStore mConfigurationStore;

    @Test
    public void testGetUUID(){
        //Instantiating with a UUID should yield that UUID
        UUID uuid = UUID.randomUUID();
        mPerson = Person.getPerson(mContext, uuid);
        assertThat(mPerson.getUUID()).isEqualTo(uuid);
    }

    @Test
    public void testJSONInstantiation() throws JSONException{
        //Put some Person data into configuration store
        //(don't test the configuration store here!)
        UUID uid = new UUID(0, 100);
        mConfigurationStore.setData(mContext, new JSONObject("{'data':{'people':[{'name':'testname', 'uid':'"+uid.toString()+"'}]}}"));

        //Instantiate a person
        mPerson = Person.getPerson(mConfigurationStore, uid);

        //Person should have the given test name
        assertThat(mPerson.getName()).isEqualTo("testname");
    }

    @Test
    public void testSingleton(){
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        //Instantiate a Person with id1
        Person person1 = Person.getPerson(mContext, id1);

        //Getting that ID again should be the same person
        assertThat(Person.getPerson(mContext, id1)).isEqualTo(person1);

        //Getting a different ID should not be the same person
        assertThat(Person.getPerson(mContext, id2)).isNotEqualTo(person1);
    }

    @Test
    public void testEquality(){
        //Call EqualsVerifier
        Place place1 = mock(Place.class);
        Place place2 = mock(Place.class);

        ConfigurationStore config1 = mock(ConfigurationStore.class);
        ConfigurationStore config2 = mock(ConfigurationStore.class);

        EqualsVerifier.forClass(Person.class)
                .withPrefabValues(Place.class, place1, place2)
                .withPrefabValues(ConfigurationStore.class, config1, config2)
                .withIgnoredFields("mLocation") //Don't do equality on locations for people - it's mutable
                .withIgnoredFields("mListener") //Don't do equality on listeners - it's mutable
                .withIgnoredFields("mName") //Don't do equality on name - UID is enough
                .verify();
    }

    @Test
    public void testGetLocation(){
        Place place1 = mock(Place.class);
        Place place2 = mock(Place.class);
        mPerson = Person.getPerson(mContext, UUID.randomUUID());

        //Setting location then immediately getting it should yield that location
        mPerson.setLocation(place1);
        assertThat(mPerson.getLocation()).isEqualTo(place1);

        //Changing location should yield the new location
        mPerson.setLocation(place2);
        assertThat(mPerson.getLocation()).isEqualTo(place2);
    }

    @Test
    public void testLocationListener(){
        mPerson = Person.getPerson(mContext, UUID.randomUUID());
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

import android.content.Context;

import com.clquebec.framework.HTTPRequestQueue;
import com.clquebec.framework.controllable.ControllableDevice;
import com.clquebec.framework.location.Building;
import com.clquebec.framework.location.Room;
import com.clquebec.framework.storage.ConfigurationStore;
import com.clquebec.framework.storage.ConfigurationStore.ConfigurationAvailableCallback;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 20/02/18
 */

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationStoreTest extends TestCase {

    //Injected into mConfigurationStore
    @Mock(name="queue")
    private HTTPRequestQueue requestQueue;

    @Mock(name="c")
    private Context mContext;

    @InjectMocks
    private ConfigurationStore mConfigurationStore;

    @Test
    public void testSingleton(){
        //The ConfigurationStore has already been injected with mocks
        assertThat(mConfigurationStore).isNotNull();

        //Make two stores
        ConfigurationStore store1 = ConfigurationStore.getInstance(mContext);
        ConfigurationStore store2 = ConfigurationStore.getInstance(mContext);

        //Are they the same?
        assertThat(store1).isEqualTo(store2);
    }

    @Test
    public void testCallback() throws JSONException{
        //Make a mock callback
        ConfigurationAvailableCallback callback =
                mock(ConfigurationAvailableCallback.class);

        //Assign callback
        mConfigurationStore.onConfigAvailable(callback);

        //Callback should not have been run - no data
        verify(callback, times(0)).onConfigurationAvailable(any());

        //Give some test data
        mConfigurationStore.setData(mContext, new JSONObject("{}"));

        //Callback should have been run once
        verify(callback, times(1)).onConfigurationAvailable(mConfigurationStore);

        //Change data again
        mConfigurationStore.setData(mContext, new JSONObject( "{'data':{'name':'testname'}}"));

        //Callback should still have only been run once - it's a one-time thing
        verify(callback, times(1)).onConfigurationAvailable(mConfigurationStore);

        //Re-attach callback
        mConfigurationStore.onConfigAvailable(callback);

        //Callback should have been called again
        verify(callback, times(2)).onConfigurationAvailable(mConfigurationStore);

        //Again, changing data should not call the callback:
        mConfigurationStore.setData(mContext, new JSONObject("{'data':{'name':'testname', 'uid':1}}"));
        verify(callback, times(2)).onConfigurationAvailable(mConfigurationStore);

        //Reset data
        mConfigurationStore.setData(mContext, null);

        //Attach many callbacks
        Set<ConfigurationAvailableCallback> callbackSet = new HashSet<>();
        for(int i = 0; i < 10; i++){
            ConfigurationAvailableCallback newCallback = mock(ConfigurationAvailableCallback.class);
            callbackSet.add(newCallback);
            mConfigurationStore.onConfigAvailable(newCallback);
        }

        //None of the callbacks should have been run
        for(ConfigurationAvailableCallback c : callbackSet){
            verify(c, times(0)).onConfigurationAvailable(any());
        }

        //Set some data - all callbacks should be run once:
        mConfigurationStore.setData(mContext, new JSONObject("{}"));
        for(ConfigurationAvailableCallback c : callbackSet){
            verify(c, times(1)).onConfigurationAvailable(mConfigurationStore);
        }
    }

    @Test
    public void testSetData() throws JSONException{
        //Setting data as null should be allowed
        //(Testing of callback behaviour is in testCallback)
        mConfigurationStore.setData(mContext, null);

        //Give some people data:
        UUID id1 = new UUID(0, 1);
        UUID id2 = new UUID(0, 2);

        JSONObject configData = new JSONObject();
        JSONObject peopleData = new JSONObject();
        JSONArray peopleArray = new JSONArray();

        JSONObject person1 = new JSONObject();
        person1.put("name", "testname");
        person1.put("uid", 1);

        JSONObject person2 = new JSONObject();
        person2.put("name", "testname2");
        person2.put("uid", 2);

        peopleArray.put(person1);
        peopleArray.put(person2);
        peopleData.put("people", peopleArray);
        configData.put("data", peopleData);

        //Set data
        mConfigurationStore.setData(mContext, configData);

        //Check data is available
        assertThat(mConfigurationStore.getPersonInformation(id1)).isNotNull();
        assertThat(mConfigurationStore.getPersonInformation(id2)).isNotNull();
        assertThat(mConfigurationStore.getPersonInformation(new UUID(0, 3))).isNull();

        //Check that the data has come out correctly:
        assertThat(
                mConfigurationStore.getPersonInformation(id1)
                .getString("name"))
                .isEqualTo("testname");
        assertThat(
                mConfigurationStore.getPersonInformation(id2)
                        .getString("name"))
                .isEqualTo("testname2");
    }

    @Test
    public void testGetBuilding() throws JSONException{
        //Put in some building data:
        mConfigurationStore.setData(mContext, new JSONObject("{'data':{'rooms':[{'name':'TestRoom', 'uid':10}]}}"));

        //Check that building has 1 room with a name TestRoom
        Building building = mConfigurationStore.getBuilding(mContext);

        assertThat(building.getRooms().size()).isEqualTo(1);
        for(Room r : building.getRooms()){
            assertThat(r.getName()).isEqualTo("TestRoom");
            assertThat(r.getID()).isEqualTo(new UUID(0, 10));
        }
    }

    @Test
    public void testGetDevices() throws JSONException{
        //Put in some device data
        mConfigurationStore.setData(mContext, new JSONObject("{'data':{'devices':[{'type':'DummyControllable', 'uid':10, 'config':{}}]}}"));

        //Check that a wrong ID returns null:
        assertThat(mConfigurationStore.getDevice(new UUID(0, 0))).isNull();

        UUID id = new UUID(0, 10);

        //Check that we get our device back on the right ID
        ControllableDevice d = mConfigurationStore.getDevice(id);
        assertThat(d).isNotNull();
        assertThat(d).isInstanceOf(ControllableDevice.class);
        assertThat(d.getID()).isEqualTo(id);

        //Check that getting the ID again results in the same Device
        assertThat(mConfigurationStore.getDevice(id)).isSameAs(d);
    }
}

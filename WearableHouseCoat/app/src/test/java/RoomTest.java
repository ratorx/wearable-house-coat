import android.content.Context;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.location.Room;
import clquebec.com.implementations.controllable.DummyControllable;
import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 19/02/18
 */

@RunWith(MockitoJUnitRunner.class)
public class RoomTest extends TestCase {
    //Test the methods of Room

    @Mock
    private Context mContext;

    private String mTestName = "TestRoom";
    private Room mRoom;

    @Test
    public void testEquals(){
        EqualsVerifier.forClass(Room.class);
    }

    @Test
    public void testGetName(){
        //Instantiation should set name
        mRoom = new Room(mContext, mTestName);
        assertThat(mRoom.getName()).isEqualTo(mTestName);

        //Set name should set name
        String newName = "NewTestName";
        mRoom.setName(newName);
        assertThat(mRoom.getName()).isEqualTo(newName);
    }

    @Test
    public void testGetDevices() throws JSONException {
        //Instantiate a room from some JSON
        mRoom = new Room(mContext, new JSONObject(
                "{'name':'testroom', 'id': 1, 'devices':[{'type': 'DummyControllable', 'config': {}}]}"
        ));

        //Check that all was instantiated correctly
        assertThat(mRoom.getID()).isEqualTo(new UUID(0, 1));
        assertThat(mRoom.getName()).isEqualTo("testroom");
        assertThat(mRoom.getDevices().size()).isEqualTo(1);

        ControllableDevice d = mRoom.getDevices().get(0);
        assertThat(d).isInstanceOf(DummyControllable.class);

        //Check that the "location" string was passed through by Room correctly.
        assertThat(((DummyControllable) d).getLocation()).isEqualTo("testroom");
    }

    @Test
    public void testGetType(){
        mRoom = new Room(mContext, mTestName);

        //Type should be null - if a Room type is ever added, change this test.
        assertThat(mRoom.getType()).isNull();
    }
}

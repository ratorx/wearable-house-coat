import android.content.Context;

import junit.framework.TestCase;

import clquebec.com.framework.location.Room;
import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 19/02/18
 */

public class RoomTest extends TestCase {
    //Test the methods of Room

    private String mTestName = "TestRoom";
    private Room mRoom;

    public void testEquals(){
        EqualsVerifier.forClass(Room.class);
    }

    public void testGetName(){
        Context c = mock(Context.class);

        //Instantiation should set name
        mRoom = new Room(c, mTestName);
        assertThat(mRoom.getName()).isEqualTo(mTestName);

        //Set name should set name
        String newName = "NewTestName";
        mRoom.setName(newName);
        assertThat(mRoom.getName()).isEqualTo(newName);
    }

    public void testGetPeople(){
        //TODO: When Room has more than an example implementation, do this.
    }

    public void testGetDevices(){
        //TODO: When Room has more than an example implementation, do this.
    }

    public void testGetType(){
        Context c = mock(Context.class);
        mRoom = new Room(c, mTestName);

        //Type should be null - if a Room type is ever added, change this test.
        assertThat(mRoom.getType()).isNull();
    }
}

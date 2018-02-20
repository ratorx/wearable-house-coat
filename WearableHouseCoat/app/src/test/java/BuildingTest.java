import android.content.Context;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.location.Building;
import clquebec.com.framework.location.Room;
import clquebec.com.framework.people.Person;
import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 19/02/18
 */

public class BuildingTest extends TestCase {
    private Building mBuilding;
    private Context mContext;
    private String mTestName = "TestRoom";

    public void testEquals(){
        EqualsVerifier.forClass(Building.class);
    }

    public void testGetName(){
        mContext = mock(Context.class);

        //Instantiation should set name
        mBuilding = new Building(mContext, mTestName);
        assertThat(mBuilding.getName()).isEqualTo(mTestName);

        //Set name should set name
        String newName = "NewTestName";
        mBuilding.setName(newName);
        assertThat(mBuilding.getName()).isEqualTo(newName);
    }

    public void testGetRooms(){
        mContext = mock(Context.class);

        //Some test rooms
        Room room1 = new Room(mContext, "TestRoom1");
        Room room2 = new Room(mContext, "TestRoom2");
        Room room3 = new Room(mContext, "TestRoom3");

        //Instantiation should set Rooms
        Set<Room> myRooms = new HashSet<>();
        myRooms.add(room1);
        myRooms.add(room2);
        mBuilding = new Building(mContext, mTestName, myRooms);
        assertThat(mBuilding.getRooms()).isEqualTo(myRooms);

        //Should not be able to modify the set used to instantiate:
        myRooms.add(room3);
        assertThat(mBuilding.getRooms()).isNotEqualTo(myRooms);

        //Should not be able to modify the Set retrieved from getRooms
        mBuilding.getRooms().add(room3);
        assertThat(mBuilding.getRooms()).isNotEqualTo(myRooms);

        //Adding rooms should return the full set on getRooms
        mBuilding.addRoom(room3);
        assertThat(mBuilding.getRooms()).isEqualTo(myRooms);
    }

    public void testGetDevices(){
        mContext = mock(Context.class);

        //Some test rooms
        Room room1 = spy(new Room(mContext, "TestRoom1"));
        Room room2 = spy(new Room(mContext, "TestRoom2"));
        Room room3 = spy(new Room(mContext, "TestRoom3"));

        //Fill the rooms with fake devices
        List<ControllableDevice> room1devs = new ArrayList<>();
        List<ControllableDevice> room2devs = new ArrayList<>();
        List<ControllableDevice> room3devs = new ArrayList<>();

        room1devs.add(mock(ControllableDevice.class));
        room2devs.add(mock(ControllableDevice.class));
        room3devs.add(mock(ControllableDevice.class));

        //Override default Room implementation
        doReturn(room1devs).when(room1).getDevices();
        doReturn(room2devs).when(room2).getDevices();
        doReturn(room3devs).when(room3).getDevices();

        //Instantiate building
        Set<Room> myRooms = new HashSet<>();
        myRooms.add(room1);
        myRooms.add(room2);
        mBuilding = new Building(mContext, mTestName, myRooms);

        //getDevices() should contain all the devices from the rooms in it
        List<ControllableDevice> includedDevices = new ArrayList<>();
        for(Room r : myRooms){
            includedDevices.addAll(r.getDevices());
        }
        assertThat(mBuilding.getDevices().containsAll(includedDevices)).isTrue();

        //Adding a room should add its devices
        mBuilding.addRoom(room3);
        assertThat(mBuilding.getDevices().containsAll(room3.getDevices())).isTrue();

        //Devices Set should be immutable
        ControllableDevice mockDevice = mock(ControllableDevice.class);
        mBuilding.getDevices().add(mockDevice);
        assertThat(mBuilding.getDevices().contains(mockDevice)).isFalse();
    }


}

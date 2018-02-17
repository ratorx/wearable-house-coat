package clquebec.com.wearablehousecoat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import clquebec.com.framework.location.Building;
import clquebec.com.framework.location.Place;
import clquebec.com.framework.location.Room;
import clquebec.com.framework.location.LocationGetter;
import clquebec.com.framework.people.Person;
import clquebec.com.implementations.location.FINDLocationProvider;
import clquebec.com.wearablehousecoat.components.DeviceTogglesAdapter;

public class MainActivity extends WearableActivity{
    private final static int ROOM_CHANGE_REQUEST = 0; //Request ID for room selector
    public static final int POLLDELAYMILLIS = 5000;

    private RecyclerView mToggleButtons;
    private DeviceTogglesAdapter mToggleAdapter;
    private TextView mLocationNameView;
    private BoxInsetLayout mContainerView;
    private FrameLayout mIAmHereWrapper;
    private LocationGetter mLocationProvider;
    private View mChangeLocationView;

    private Building mBuilding;
    private Room mCurrentDisplayedRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "This should be printed right?");
        //SECTION: Initialize Building
        //TODO: Read in from somewhere (e.g web)

        mBuilding = new Building(this, "My House");
        mBuilding.addRoom(new Room(this, "Kitchen"));
        mBuilding.addRoom(new Room(this, "Living Room"));
        mBuilding.addRoom(new Room(this, "Dungeon"));

        //END SECTION

        //SECTION: Initialize toggle button grid
        mToggleButtons = findViewById(R.id.main_togglebuttons);
        mContainerView = findViewById(R.id.main_container);

        //Set grid to have width 2
        mToggleButtons.setLayoutManager(new GridLayoutManager(this, 2));

        //Make a dummy Room with a light switch for testing
        Room room = new Room(this, "Test Room");
        mCurrentDisplayedRoom = room;

        //Attach the adapter which automatically fills with controls for current Place
        mToggleAdapter = new DeviceTogglesAdapter(mCurrentShowingPlace);
        mToggleButtons.setAdapter(mToggleAdapter); //Attach
        //END SECTION

        //SECTION: Initialize locations and location provider
        mLocationNameView = findViewById(R.id.main_currentlocation);
        TextViewCompat.setAutoSizeTextTypeWithDefaults(mLocationNameView, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        //Initialise location provider
        Person me = new Person("tcb");
        mLocationProvider = new FINDLocationProvider(this, me);
        mLocationProvider.setLocationChangeListener((user, oldLocation, newLocation) -> {
                if( user.equals(me) && mCurrentDisplayedRoom.equals(oldLocation)){ //If the user is me
                    setRoom(room, false);
                }
            }
        );

        //END SECTION
        mIAmHereWrapper = findViewById(R.id.iamhere_wrapper);

        //SECTION: Allow user to change location
        mChangeLocationView = findViewById(R.id.main_currentlocationlayout);
        mChangeLocationView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RoomSelectionActivity.class);

            //Get room names as strings
            List<CharSequence> roomNames = mBuilding.getRooms().stream()
                    .map(Room::getName).collect(Collectors.toList());

            //Pass room names as an extra
            intent.putExtra(RoomSelectionActivity.INTENT_ROOMS_EXTRA, new ArrayList<>(roomNames));
            MainActivity.this.startActivityForResult(intent, ROOM_CHANGE_REQUEST);
        });

        // Set up location update

            Log.d("LocationUpdater", "Using timer");
            mLocationUpdateHandler.post(new Runnable() {
                @Override
                public void run() {
                    mLocationProvider.forceLocationRefresh();
                    mLocationProvider.update();
                    mLocationUpdateHandler.postDelayed(this, POLLDELAYMILLIS);
                }
            });

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ROOM_CHANGE_REQUEST){
            //If a result was given, get the Room name, and call setRoom with the Room.
            if(resultCode == RESULT_OK){
                if(data != null && data.getExtras() != null){
                    String name = data.getExtras().getString(RoomSelectionActivity.INTENT_ROOM_NAME);

                    //Get first Room with that name in our building
                    Room chosenRoom = (Room) mBuilding.getRooms().stream()
                            .filter(room -> room.getName().equals(name)).toArray()[0];

                    setRoom(chosenRoom);
                }
            }
        }
    }

    public void setRoom(Place room){
        setRoom(room, true);
    }
    
    public void setRoom(Room room, boolean showIAmHere){
        //Update the location text. This needs to be converted to upper case because of a bug
        //in android with text upper case and resizing
        mLocationNameView.setText(room.getName().toUpperCase());

        mCurrentDisplayedRoom = room;
        //This automatically populates and attaches devices to buttons.
        mToggleButtons.swapAdapter(new DeviceTogglesAdapter(room), false);
    
        // Show the "I am here" button for 4 seconds
        if(showIAmHere) {
            mIAmHereWrapper.setVisibility(View.VISIBLE);
            Timer mHereTimer = new Timer();
            mHereTimer.schedule(new TimerTask() {
                public void run() {
                    runOnUiThread(() -> mIAmHereWrapper.setVisibility(View.GONE));
                }
            }, 4000);
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        //Low battery consumption in ambient mode
        mContainerView.setBackgroundColor(Color.BLACK);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        //restore to default background color.
        mContainerView.setBackgroundColor(getResources().getColor(R.color.eerie_black, getTheme()));
    }

}

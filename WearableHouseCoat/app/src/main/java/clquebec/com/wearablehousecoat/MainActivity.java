package clquebec.com.wearablehousecoat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
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
import clquebec.com.framework.people.Person;
import clquebec.com.framework.storage.ConfigurationStore;
import clquebec.com.implementations.location.FINDLocationProvider;
import clquebec.com.wearablehousecoat.components.DeviceTogglesAdapter;

public class MainActivity extends WearableActivity implements SensorEventListener{
    private final static String TAG = "MainActivity";

    private final static int ROOM_CHANGE_REQUEST = 0; //Request ID for room selector
    private final static int POLLDELAYMILLIS = 5000;

    private RecyclerView mToggleButtons;
    private TextView mLocationNameView;
    private BoxInsetLayout mContainerView;
    private FrameLayout mIAmHereWrapper;
    private FINDLocationProvider mLocationProvider;
    private final Handler mLocationUpdateHandler = new Handler();

    private Building mBuilding;
    private Place mCurrentDisplayedRoom;

    private SensorManager mSensorManager;
    private float mLastAccelSquare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SECTION: Initialize Building
        mBuilding = new Building(this, "Loading"); //Placeholder building
        //END SECTION

        //SECTION: Load in from config store
        ConfigurationStore.getInstance(this).onConfigAvailable(config -> {
            mBuilding = config.getBuilding(this);

            //Initialise "me"
            Person me = Person.getPerson(this, config.getMyUUID());
            me.setLocationListener((user, oldLocation, newLocation) -> {
                if (mCurrentDisplayedRoom.equals(oldLocation)) {
                    setRoom(newLocation, false);
                }
            });

            //Initialise location provider
            mLocationProvider = new FINDLocationProvider(this, me);

            // Set up location update
            //Use the 'best' method for location update available:
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            if(mSensorManager == null || mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null){
                Log.d(TAG, "Using timer for location updater");
                mLocationUpdateHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLocationProvider.refreshLocations();
                        mLocationProvider.update();
                        mLocationUpdateHandler.postDelayed(this, POLLDELAYMILLIS);
                    }
                });
            }else{
                if(mSensorManager != null){
                    Log.d(TAG, "Using accelerometer for location updater");

                    mLastAccelSquare = 0;

                    mSensorManager.registerListener(this,
                            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                            SensorManager.SENSOR_DELAY_NORMAL,
                            0);
                }
            }
        });
        //END SECTION

        //SECTION: Initialize toggle button grid
        mToggleButtons = findViewById(R.id.main_togglebuttons);
        mContainerView = findViewById(R.id.main_container);

        //Set grid to have width 2
        mToggleButtons.setLayoutManager(new GridLayoutManager(this, 2));

        //Attach the adapter which automatically fills with controls for current Place
        DeviceTogglesAdapter mToggleAdapter = new DeviceTogglesAdapter(null);
        mToggleButtons.setAdapter(mToggleAdapter); //Attach
        //END SECTION

        //SECTION: Initialize locations and location provider
        mLocationNameView = findViewById(R.id.main_currentlocation);
        TextViewCompat.setAutoSizeTextTypeWithDefaults(mLocationNameView, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        //END SECTION

        mIAmHereWrapper = findViewById(R.id.iamhere_wrapper);
        mIAmHereWrapper.setVisibility(View.GONE);

        //On click, calibrate location provider
        findViewById(R.id.iamhere_wrapper)
                .setOnClickListener(view -> mLocationProvider.calibrate(mCurrentDisplayedRoom));

        // Enables Always-on
        setAmbientEnabled();

        //SECTION: Allow user to change location
        View mChangeLocationView = findViewById(R.id.main_currentlocationlayout);
        mChangeLocationView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RoomSelectionActivity.class);

            //Get room names as strings
            List<CharSequence> roomNames = mBuilding.getRooms().stream()
                    .map(Room::getName).collect(Collectors.toList());

            //Pass room names as an extra
            intent.putExtra(RoomSelectionActivity.INTENT_ROOMS_EXTRA, new ArrayList<>(roomNames));
            MainActivity.this.startActivityForResult(intent, ROOM_CHANGE_REQUEST);
        });

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ROOM_CHANGE_REQUEST) {
            //If a result was given, get the Room name, and call setRoom with the Room.
            if (resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null) {
                    String name = data.getExtras().getString(RoomSelectionActivity.INTENT_ROOM_NAME);

                    //Get first Room with that name in our building
                    Room chosenRoom = (Room) mBuilding.getRooms().stream()
                            .filter(room -> room.getName().equals(name)).toArray()[0];

                    setRoom(chosenRoom);
                }
            }
        }
    }

    public void setRoom(Place room) {
        setRoom(room, true);
    }

    public void setRoom(Place room, boolean showIAmHere) {
        mCurrentDisplayedRoom = room;
        //Update the location text. This needs to be converted to upper case because of a bug
        //in android with text upper case and resizing
        mLocationNameView.setText(room.getName().toUpperCase());

        mCurrentDisplayedRoom = room;
        //This automatically populates and attaches devices to buttons.
        mToggleButtons.swapAdapter(new DeviceTogglesAdapter(room), false);

        // Show the "I am here" button for 4 seconds
        if (showIAmHere) {
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            //Calculate magnitude
            float mag = 0;
            for(float axis : sensorEvent.values){
                mag += axis*axis;
            }

            //Compare with previous
            if(Math.abs(mag - mLastAccelSquare) > 20){
                //Do a location refresh on every step
                Log.d(TAG, "Refreshing locations");
                mLocationProvider.refreshLocations();
                mLocationProvider.update();
            }

            mLastAccelSquare = mag;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Do nothing
    }
}

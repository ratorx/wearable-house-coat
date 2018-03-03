package com.clquebec.wearablehousecoat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.clquebec.framework.location.Building;
import com.clquebec.framework.location.Place;
import com.clquebec.framework.location.Room;
import com.clquebec.framework.people.Person;
import com.clquebec.framework.storage.ConfigurationStore;
import com.clquebec.implementations.location.FINDLocationProvider;
import com.clquebec.wearablehousecoat.components.DeviceTogglesAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.philips.lighting.hue.sdk.wrapper.Persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class MainActivity extends WearableActivity implements SensorEventListener{
    private final static String TAG = "MainActivity";

    private final static int ROOM_CHANGE_REQUEST = 0; //Request ID for room selector
    private final static int GOOGLE_SIGN_IN_REQUEST = 1; //Request Google Sign-in
    private final static int LOCATION_PERMISSION_REQUEST = 2; //Request Location permission
    private final static int POLLDELAYMILLIS = 5000;

    private RecyclerView mToggleButtons;
    private TextView mLocationNameView;
    private BoxInsetLayout mContainerView;
    private FrameLayout mIAmHereWrapper;
    private FINDLocationProvider mLocationProvider;
    private final Handler mLocationUpdateHandler = new Handler();

    private Building mBuilding;
    private Place mCurrentDisplayedRoom;
    private Person mMe;
    private long mLastLocationUpdate;

    //load HueSDK on startup
    static {
        System.loadLibrary("huesdk");
    }

    private SensorManager mSensorManager;
    private float mLastAccelSquare;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mAccount;
    private View mSetCurrentLocationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Persistence.setStorageLocation(getFilesDir().getAbsolutePath(), "HueWear");
        setContentView(R.layout.activity_main);

        mLocationNameView = findViewById(R.id.main_currentlocation);

        //SECTION: Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //SECTION: Initialize Building
        mBuilding = new Building(this, "Loading"); //Placeholder building
        //END SECTION

        //SECTION: Load in from config store
        ConfigurationStore.getInstance(this).onConfigAvailable(config -> {
            mBuilding = config.getBuilding(this);

            //Initialise "me"
            mMe = Person.getPerson(this, config.getMyUUID());
            mMe.setLocationListener((user, oldLocation, newLocation) -> {
                if(newLocation != null) {
                    Log.d(TAG, "Location for me: " + newLocation.getName());
                    if (mCurrentDisplayedRoom == null || mCurrentDisplayedRoom.equals(oldLocation)) {
                        setRoom(newLocation, false);
                    }
                }
            });

            //Initialise location provider
            mLocationProvider = new FINDLocationProvider(this, mMe);

            //Get an initial update
            mLocationProvider.update();

            // Set up location update
            //Use the 'best' method for location update available:
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            if(mSensorManager == null || mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null){
                Log.d(TAG, "Using timer for location updater");
                mLocationUpdateHandler.post(new Runnable() {
                    @Override
                    public void run() {
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
        TextViewCompat.setAutoSizeTextTypeWithDefaults(mLocationNameView, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        //END SECTION

        mIAmHereWrapper = findViewById(R.id.iamhere_wrapper);
        mIAmHereWrapper.setVisibility(View.GONE);

        //On click, calibrate location provider
        findViewById(R.id.iamhere_button).setOnClickListener(view -> {
            mLocationProvider.calibrate(mCurrentDisplayedRoom);
            mIAmHereWrapper.setVisibility(View.GONE);
        });

        // Enables Always-on
        setAmbientEnabled();

        //SECTION: Allow user to change location
        View mChangeLocationView = findViewById(R.id.main_changelocationview);
        mChangeLocationView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RoomSelectionActivity.class);

            //Try and re-get the configuration store
            if (mBuilding.getRooms().size() == 0) {
                ConfigurationStore.getInstance(this).tryGetConfigFromServer(this);
            }

            //Get room names as strings
            ConfigurationStore.getInstance(this).onConfigAvailable(config -> {
                if (mBuilding.getRooms().size() == 0) {
                    mBuilding = config.getBuilding(this);
                }

                List<CharSequence> roomNames = mBuilding.getRooms().stream()
                        .map(Room::getName).collect(Collectors.toList());

                //Pass room names as an extra
                intent.putExtra(RoomSelectionActivity.INTENT_ROOMS_EXTRA, new ArrayList<>(roomNames));
                MainActivity.this.startActivityForResult(intent, ROOM_CHANGE_REQUEST);
            });
        });

        mSetCurrentLocationView = findViewById(R.id.main_switchcurrentlocation);
        mSetCurrentLocationView.setOnClickListener(view -> {
            if(mMe != null) {
                setRoom(mMe.getLocation(), false);
                mLocationProvider.update();
            }
        });


        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onStart(){
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount mAccount = GoogleSignIn.getLastSignedInAccount(this);

        if(mAccount == null){
            //Start Google Sign-In flow
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST);
        }else{
            Log.d(TAG, "Signed in with "+mAccount.getEmail());
            ConfigurationStore.getInstance(this).setMyEmail(mAccount.getEmail());

            //Check for Location permission, and request them if not granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST
                );
            }
        }
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
        }else if(requestCode == GOOGLE_SIGN_IN_REQUEST) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                mAccount = task.getResult(ApiException.class);
                ConfigurationStore.getInstance(this).setMyEmail(mAccount.getEmail());

                //Check for Location permission, and request them if not granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST
                    );
                }
            } catch (ApiException e) {
                Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
                //TODO: Show an error / exit app.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Permission granted
        }else{
            Log.e(TAG, "Location permission was not given");
            //TODO: Show an error / exit app.
        }
    }

    public void setRoom(Place room) {
        setRoom(room, true);
    }

    public void setRoom(Place room, boolean showIAmHere) {
        mCurrentDisplayedRoom = room;

        if(room != null) {
            //Update the location text. This needs to be converted to upper case because of a bug
            //in android with text upper case and resizing
            mLocationNameView.setText(room.getName().toUpperCase());

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

            //Don't show Current Location Button if we're displaying the current room
            if(mMe != null){
                if(Objects.equals(mMe.getLocation(), mCurrentDisplayedRoom)){
                    mSetCurrentLocationView.setVisibility(View.INVISIBLE);
                }else{
                    mSetCurrentLocationView.setVisibility(View.VISIBLE);
                }
            }else{
                mSetCurrentLocationView.setVisibility(View.VISIBLE);
            }
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
            if(System.nanoTime() - mLastLocationUpdate > 1000) {
                mLastLocationUpdate = System.nanoTime();

                //Calculate magnitude
                float mag = 0;
                for (float axis : sensorEvent.values) {
                    mag += axis * axis;
                }

                //Compare with previous
                if (Math.abs(mag - mLastAccelSquare) > 20) {
                    //Do a location refresh on every step
                    mLocationProvider.update();
                }

                mLastAccelSquare = mag;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Do nothing
    }
}

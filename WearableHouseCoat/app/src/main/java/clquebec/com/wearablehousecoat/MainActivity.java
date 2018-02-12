package clquebec.com.wearablehousecoat;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Set;

import clquebec.com.framework.location.IndoorLocationProvider;
import clquebec.com.framework.location.Room;
import clquebec.com.framework.people.Person;
import clquebec.com.implementations.location.DummyLocationProvider;
import clquebec.com.wearablehousecoat.components.DeviceTogglesAdapter;

public class MainActivity extends WearableActivity {

    private RecyclerView mToggleButtons;
    private DeviceTogglesAdapter mToggleAdapter;
    private TextView mLocationNameView;
    private TextView mPersonCountView;

    private IndoorLocationProvider mLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SECTION: Initialize toggle button grid
        mToggleButtons = findViewById(R.id.main_togglebuttons);

        //Set grid to have width 2
        mToggleButtons.setLayoutManager(new GridLayoutManager(this, 2));

        //Make a dummy Room with a light switch for testing
        Room room = new Room(this, "Test Room");

        //Attach the adapter which automatically fills with controls for current Place
        mToggleAdapter = new DeviceTogglesAdapter(room);
        mToggleButtons.setAdapter(mToggleAdapter); //Attach
        //END SECTION

        //SECTION: Initialize locations and location provider
        mLocationNameView = findViewById(R.id.main_currentlocation);

        //Create a dummy location provider to give us dummy information
        mLocationProvider = new DummyLocationProvider(this);

        //Register a listener so that information is updated on location change.
        mLocationProvider.setLocationChangeListener((oldLocation, newLocation) -> {
                    //Set location text to the right location
                    mLocationNameView.setText(newLocation.getName());

                    //This automatically populates and attaches devices to buttons.
                    mToggleButtons.swapAdapter(new DeviceTogglesAdapter(newLocation), false);
                }
        );


        //END SECTION

        // Enables Always-on
        setAmbientEnabled();

        /* This code is not dynamic - great for testing but not something to keep.
        Button mHueButton = findViewById(R.id.hue_button);
        mHueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View hue_control = findViewById(R.id.hue_control);
                hue_control.setVisibility(View.VISIBLE);
            }
        });

        //TODO: swipe to close.
        View mainLayout = findViewById(R.id.hue_control);
        mainLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                View hue_control = findViewById(R.id.hue_control);
                hue_control.setVisibility(View.GONE);
            }
        });
        */
    }
}

package clquebec.com.wearablehousecoat;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import java.util.Set;

import clquebec.com.framework.location.IndoorLocationProvider;
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

        //Initialise Views (UI components)
        mLocationNameView = findViewById(R.id.main_currentlocation);
        mPersonCountView = findViewById(R.id.main_companions);
        mToggleButtons = findViewById(R.id.main_togglebuttons);

        //Make a new grid of with width 3
        mToggleButtons.setLayoutManager(new GridLayoutManager(this, 3));

        //Attach the adapter which automatically fills with controls for current Place
        mToggleAdapter = new DeviceTogglesAdapter(null); //No Place provided yet
        mToggleButtons.setAdapter(mToggleAdapter);

        //Initialise location provider
        mLocationProvider = new DummyLocationProvider(this);
        mLocationProvider.setLocationChangeListener((oldLocation, newLocation) -> {
                mLocationNameView.setText(newLocation.getName());

                Set<Person> people = newLocation.getPeople();
                mPersonCountView.setText(getResources().getQuantityString( //Automatically varies based on number
                                R.plurals.companion_strings,
                                people.size(),
                                people.size()
                        ));

                //This automatically populates and attaches devices to buttons.
                mToggleButtons.swapAdapter(new DeviceTogglesAdapter(newLocation), false);
            }
        );

        // Enables Always-on
        setAmbientEnabled();
    }
}

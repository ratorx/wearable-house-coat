package clquebec.com.wearablehousecoat;

import android.bluetooth.BluetoothClass;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Set;

import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllableLightDevice;
import clquebec.com.framework.location.IndoorLocationProvider;
import clquebec.com.framework.location.LocationChangeListener;
import clquebec.com.framework.location.Place;
import clquebec.com.framework.location.Room;
import clquebec.com.framework.people.Person;
import clquebec.com.implementations.controllable.IFTTTLight;
import clquebec.com.implementations.location.DummyLocationProvider;
import clquebec.com.wearablehousecoat.components.DeviceControlButton;

public class MainActivity extends WearableActivity {

    private DeviceControlButton mTestButton;
    private TextView mLocationNameView;
    private TextView mPersonCountView;

    private IndoorLocationProvider mLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationNameView = findViewById(R.id.main_currentlocation);
        mPersonCountView = findViewById(R.id.main_companions);
        mTestButton = findViewById(R.id.lightButton);

        mLocationProvider = new DummyLocationProvider(this);
        mLocationProvider.setLocationChangeListener((oldLocation, newLocation) -> {
                mLocationNameView.setText(newLocation.getName());

                Set<Person> people = newLocation.getPeople();
                mPersonCountView.setText(
                        getResources().getQuantityString( //Automatically varies based on number
                                R.plurals.companion_strings,
                                people.size(),
                                people.size()
                        ));

                //TODO: Automatically generate buttons based on what's in room.
                //For now, for testing, it seems fine to work without this.

                //TODO: Attach each device to a different button - probably done in previous step.
                for(ControllableDevice device: newLocation.getDevices()) {
                    mTestButton.attachDevice(device);
                }
            }
        );

        // Enables Always-on
        setAmbientEnabled();
    }
}

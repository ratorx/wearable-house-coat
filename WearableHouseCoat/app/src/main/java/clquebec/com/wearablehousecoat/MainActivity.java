package clquebec.com.wearablehousecoat;

import android.bluetooth.BluetoothClass;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllableLightDevice;
import clquebec.com.framework.location.Room;
import clquebec.com.implementations.controllable.IFTTTLight;
import clquebec.com.wearablehousecoat.components.DeviceControlButton;

public class MainActivity extends WearableActivity {

    private DeviceControlButton mTestButton;

    private Room mCurrentRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Find current room using an IndoorLocationProvider
        mCurrentRoom = new Room(this, "Test Room");

        //TODO: Automatically generate buttons based on what's in room.
        //For now, for testing, it seems fine to work without this.

        mTestButton = findViewById(R.id.lightButton);

        //TODO: Attach each device to a different button - probably done in previous step.
        for(ControllableDevice device: mCurrentRoom.getDevices()) {
            mTestButton.attachDevice(device);
        }

        // Enables Always-on
        setAmbientEnabled();
    }
}

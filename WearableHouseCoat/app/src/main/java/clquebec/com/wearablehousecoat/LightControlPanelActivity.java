package clquebec.com.wearablehousecoat;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.jraf.android.androidwearcolorpicker.app.ColorPickActivity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import clquebec.com.framework.controllable.ActionNotSupported;
import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllableLightDevice;
import clquebec.com.framework.storage.ConfigurationStore;

public class LightControlPanelActivity extends WearableActivity {
    private final static String TAG = "LightControlPanelActivity";

    private final static int REQUEST_PICK_COLOR = 1;
    public static final String ID_EXTRA = "DeviceID";

    private ImageView mColourPreview;

    private ControllableLightDevice mLightDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_control_panel);

        // Enables Always-on
        setAmbientEnabled();

        mColourPreview = findViewById(R.id.colourPreview);
        mColourPreview.setOnClickListener(view -> {
            Intent intent = new ColorPickActivity.IntentBuilder().oldColor(((Integer) mColourPreview.getTag())).build(LightControlPanelActivity.this);
            startActivityForResult(intent, REQUEST_PICK_COLOR);
        });

        //Get the required ControllableLightDevice for colour picking on
        if(getIntent().getExtras() == null){
            throw new IllegalArgumentException("LightControlPanelActivity must be given a Device ID");
        }
        UUID deviceID = (UUID) getIntent().getExtras().get(ID_EXTRA);

        ConfigurationStore.getInstance(this).onConfigAvailable(config -> {
            ControllableDevice device = config.getDevice(deviceID);

            if(device == null){
                throw new IllegalArgumentException("LightControlPanelActivity must be given the ID to a valid device");
            }

            if(!(device instanceof ControllableLightDevice)){
                throw new IllegalArgumentException("LightControlPanelActivity must be given the ID to a light device");
            }

            mLightDevice = (ControllableLightDevice) device;

            try {
                mColourPreview.setColorFilter(mLightDevice.getLightColor());
                mColourPreview.setTag(mLightDevice.getLightColor());
            }catch(ActionNotSupported e){
                Log.e(TAG, "Light does not support colours");
                mColourPreview.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICK_COLOR:
                if (resultCode == RESULT_CANCELED) {
                    // The user pressed 'Cancel'
                    break;
                }
                int pickedColor = ColorPickActivity.getPickedColor(data);

                if (mLightDevice != null){
                    try{
                        mLightDevice.setLightColor(pickedColor);

                        Timer mHereTimer = new Timer();
                        mHereTimer.schedule(new TimerTask() {
                            public void run() {
                                runOnUiThread(() -> {
                                    try {
                                        int c = mLightDevice.getLightColor();
                                        Log.d(TAG, "Set color is " + pickedColor + ", get color is " + c);
                                        mColourPreview.setColorFilter(c);
                                        mColourPreview.setTag(c);
                                    }catch(ActionNotSupported e){
                                        Log.e(TAG, "Light device does not support getting colours");
                                    }
                                });
                            }
                        }, 3000);

                    }catch (ActionNotSupported e){
                        Log.e(TAG, "Light device does not support setting colours");
                    }
                }
                break;
        }
    }
}

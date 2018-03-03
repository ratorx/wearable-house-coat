package com.clquebec.wearablehousecoat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.clquebec.framework.controllable.ActionNotSupported;
import com.clquebec.framework.controllable.ControllableDevice;
import com.clquebec.framework.controllable.ControllableLightDevice;
import com.clquebec.framework.listenable.DeviceChangeListener;
import com.clquebec.framework.listenable.ListenableDevice;
import com.clquebec.framework.storage.ConfigurationStore;

import org.jraf.android.androidwearcolorpicker.app.ColorPickActivity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class LightControlPanelActivity extends WearableActivity implements DeviceChangeListener {
    private final static String TAG = "LightControlPanelActivity";
    private final static int REQUEST_PICK_COLOR = 1;
    public static final String ID_EXTRA = "DeviceID";

    private ImageView mColourPreview;
    private SeekBar mBrightnessBar;
    private ProgressBar mSpinningProgress;
    private boolean changingBrightness = false;

    private ControllableLightDevice mLightDevice;

    private Vibrator vib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_control_panel);

        // Enables Always-on
        setAmbientEnabled();

        vib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        mColourPreview = findViewById(R.id.colourPreview);
        mBrightnessBar = findViewById(R.id.brightnessBar);
        mSpinningProgress = findViewById(R.id.spinningProgress);
        mBrightnessBar.setMax(255);
        mBrightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                changingBrightness = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Timer brightnessTimer = new Timer();
                brightnessTimer.schedule(new TimerTask() {
                    public void run() {
                        runOnUiThread(() -> {
                            changingBrightness = false;
                            try{
                                mBrightnessBar.setProgress(mLightDevice.getBrightness());
                            }catch (ActionNotSupported e) {
                                Log.e(TAG, "Device does not support getting brightness");
                            }});
                    }
                }, 500);
                try {
                    mLightDevice.setBrightness(seekBar.getProgress());
                }catch(ActionNotSupported e){
                    Log.e(TAG, "Device does not support setting brightness");
                }
            }
        });
        mColourPreview.setOnClickListener(view -> {
            Intent intent = new ColorPickActivity.IntentBuilder().oldColor(((Integer) mColourPreview.getTag())).build(LightControlPanelActivity.this);
            vib.vibrate(20);
            startActivityForResult(intent, REQUEST_PICK_COLOR);
        });

        //Get the required ControllableLightDevice for colour picking on
        if(getIntent().getExtras() == null){
            throw new IllegalArgumentException("LightControlPanelActivity must be given a Device ID");
        }
        UUID deviceID = (UUID) getIntent().getExtras().get(ID_EXTRA);

        ConfigurationStore.getInstance(this).onConfigAvailable(config -> {
            ControllableDevice device = config.getDevice(deviceID);
            if (device == null ){
                throw new IllegalArgumentException("LightControlPanelActivity must be given the ID to a valid device");
            }

            if(!(device instanceof ControllableLightDevice)){
                throw new IllegalArgumentException("LightControlPanelActivity must be given the ID to a light device");
            }

            mLightDevice = (ControllableLightDevice) device;

            if(mLightDevice instanceof ListenableDevice) {
                ((ListenableDevice) device).addListener(this);
            }

            try {
                mColourPreview.setColorFilter(mLightDevice.getLightColor());
                mColourPreview.setTag(mLightDevice.getLightColor());
            }catch(ActionNotSupported e){
                Log.e(TAG, "Light does not support colours");
                mColourPreview.setVisibility(View.GONE);
            }

            try {
                mBrightnessBar.setProgress(mLightDevice.getBrightness());
            }catch(ActionNotSupported e){
                Log.e(TAG, "Light does not support brightness");
                mBrightnessBar.setVisibility(View.GONE);
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
                        mSpinningProgress.setVisibility(View.VISIBLE);
                        mColourPreview.setColorFilter(pickedColor);

                    }catch (ActionNotSupported e){
                        Log.e(TAG, "Light device does not support setting colours");
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        //Unregister listener
        if(mLightDevice instanceof ListenableDevice){
            ((ListenableDevice) mLightDevice).removeListener(this);
        }
    }

    @Override
    public void updateState(ListenableDevice device) {
        ControllableLightDevice light = (ControllableLightDevice) device;

        if (!changingBrightness) {
            try {
                mBrightnessBar.setProgress(light.getBrightness());
            } catch (ActionNotSupported e) {
                Log.e(TAG, "Light does not support brightness");
            }
        }

        try {
            int c = light.getLightColor();
            if (c != (Integer) mColourPreview.getTag()){
                mColourPreview.setColorFilter(c);
                mColourPreview.setTag(c);
                mSpinningProgress.setVisibility(View.INVISIBLE);

            }

        } catch (ActionNotSupported e) {
            Log.e(TAG, "Light does not support colours");
        }
        Log.d(TAG, "Ran listener event");

    }
}

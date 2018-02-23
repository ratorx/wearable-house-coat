package clquebec.com.wearablehousecoat;


import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.util.UUID;

import clquebec.com.framework.controllable.ActionNotSupported;
import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllablePlaybackDevice;
import clquebec.com.framework.listenable.DeviceChangeListener;
import clquebec.com.framework.listenable.ListenableDevice;
import clquebec.com.framework.storage.ConfigurationStore;

public class MediaControlPanelActivity extends WearableActivity implements DeviceChangeListener {
    private final static String TAG = "MediaControlPanelActivity";
    public static final String ID_EXTRA = "DeviceID";
    private LinearLayout mVolumeWrapper;
    private LinearLayout mBrightnessWrapper;
    private SeekBar mVolumeBar;
    private SeekBar mBrightnessBar;
    private ControllablePlaybackDevice mPlaybackDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_control_panel);

        setAmbientEnabled();

        mVolumeBar = findViewById(R.id.volumeBar);

        if(getIntent().getExtras() == null){
            throw new IllegalArgumentException("LightControlPanelActivity must be given a Device ID");
        }
        UUID deviceID = (UUID) getIntent().getExtras().get(ID_EXTRA);
        ConfigurationStore.getInstance(this).onConfigAvailable(config -> {
            ControllableDevice device = config.getDevice(deviceID);
            if (device == null ){
                throw new IllegalArgumentException("MediaControlPanelActivity must be given the ID to a valid device");
            }

            if(!(device instanceof ControllablePlaybackDevice)){
                throw new IllegalArgumentException("MediaControlPanelActivity must be given the ID to a playback device");
            }

            mPlaybackDevice = (ControllablePlaybackDevice) device;

            if(mPlaybackDevice instanceof ListenableDevice) {
                ((ListenableDevice) device).addListener(this);
            }

            try {
                mBrightnessBar.setProgress((int) mPlaybackDevice.getBrightness());
            }catch(ActionNotSupported e){
                Log.e(TAG, "Playback device does not support brightness");
                mBrightnessBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //Unregister listener
        if(mPlaybackDevice instanceof ListenableDevice){
            ((ListenableDevice) mPlaybackDevice).removeListener(this);
        }
    }

    @Override
    public void updateState(ListenableDevice device) {

    }
}

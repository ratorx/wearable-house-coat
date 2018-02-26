package com.clquebec.wearablehousecoat;


import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.clquebec.framework.controllable.ActionNotSupported;
import com.clquebec.framework.controllable.ControllableDevice;
import com.clquebec.framework.controllable.ControllablePlaybackDevice;
import com.clquebec.framework.listenable.DeviceChangeListener;
import com.clquebec.framework.listenable.ListenableDevice;
import com.clquebec.framework.storage.ConfigurationStore;

import java.util.UUID;

public class MediaControlPanelActivity extends WearableActivity implements DeviceChangeListener {
    private final static String TAG = "MediaControlPanelActivity";
    public static final String ID_EXTRA = "DeviceID";

    private LinearLayout mVolumeWrapper;
    private LinearLayout mBrightnessWrapper;
    private SeekBar mVolumeBar;
    private SeekBar mBrightnessBar;
    private ImageView mPreviousButton;
    private ImageView mPlayPauseButton;
    private ImageView mNextButton;
    private ControllablePlaybackDevice mPlaybackDevice;
    private boolean changingVolume = false;
    private boolean changingBrightness = false;
    private boolean currentlyPlaying = false;
    private boolean hasNext;
    private boolean hasPrevious;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_control_panel);

        setAmbientEnabled();

        // Binding UI elements to useful variables

        // Volume slider
        mVolumeBar = findViewById(R.id.volumeBar);
        mVolumeBar.setMax(255);
        mVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Brightness slider
        mBrightnessBar = findViewById(R.id.brightnessBar);
        mBrightnessBar.setMax(255);
        mBrightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Play/Pause button
        mPlayPauseButton = findViewById(R.id.mediaPlayPause);
        try{
            currentlyPlaying = mPlaybackDevice.getPlaying();
            if(currentlyPlaying){
                mPlayPauseButton.setImageResource(R.drawable.ic_pause_button);
            }
        }catch(ActionNotSupported ans){
            Log.e(TAG, "PB device does not support playing status check");
        }
        mPlayPauseButton.setOnClickListener((view) -> {
            if(currentlyPlaying){
                if(mPlaybackDevice.setPlaying(false)){
                    currentlyPlaying = false;
                    mPlayPauseButton.setImageResource(R.drawable.ic_play_button);
                }
                else{
                    Log.e(TAG,"Failed to disable device playback");
                }
            }
            else{
                if(mPlaybackDevice.setPlaying(true)){
                    currentlyPlaying = true;
                    mPlayPauseButton.setImageResource(R.drawable.ic_pause_button);
                }
                else{
                    Log.e(TAG,"Failed to enable device playback");
                }
            }
        });

        /* TODO:
                Add functionality for enabling/disabling buttons based on availability
                Means updating when a new item is added to the queue (could be done by
                another user or device)
        */
        // Previous button
        mPreviousButton = findViewById(R.id.mediaLeft);
        mPreviousButton.setOnClickListener((view) -> {
            try{
                mPlaybackDevice.skipPrevious();
            }
            catch(ActionNotSupported ans){
                Log.e(TAG,"PB device does not support backwards skip");
            }
        });

        // Next button
        mNextButton = findViewById(R.id.mediaRight);
        mNextButton.setOnClickListener((view) -> {
            try{
                mPlaybackDevice.skipNext();
            }
            catch(ActionNotSupported ans){
                Log.e(TAG, "PB device does not support forwards skip");
            }
        });

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

            // A messy way of checking if we have volume and brightness enabled.
            try {
                mVolumeBar.setProgress(mPlaybackDevice.getVolume());
            }catch(ActionNotSupported e){
                Log.e(TAG, "PB device does not support volume get");
                mVolumeWrapper.setVisibility(View.GONE);
            }

            try {
                mBrightnessBar.setProgress(mPlaybackDevice.getBrightness());
            }catch(ActionNotSupported e){
                Log.e(TAG, "PB device does not support brightness get");
                mBrightnessWrapper.setVisibility(View.GONE);
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

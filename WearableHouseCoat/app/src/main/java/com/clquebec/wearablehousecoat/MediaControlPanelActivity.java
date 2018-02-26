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

import java.util.Timer;
import java.util.TimerTask;
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
    private ImageView mVolumeIcon;
    private ControllablePlaybackDevice mPlaybackDevice;
    private boolean changingVolume = false;
    private boolean changingBrightness = false;
    private boolean currentlyPlaying = false;

    private static final int barMax = 255;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_control_panel);

        setAmbientEnabled();

        // Binding UI elements to useful variables
        mVolumeIcon = findViewById(R.id.volumeIcon);
        mVolumeWrapper = findViewById(R.id.volumeControlLayout);
        mBrightnessWrapper = findViewById(R.id.brightnessControlLayout);

        // Volume slider
        mVolumeBar = findViewById(R.id.volumeBar);
        mVolumeBar.setMax(barMax);
        mVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // If this doesn't work on a listener update, copy this into the listener
                // and introduce a check here for user input (on boolean b)
                double prog = seekBar.getProgress() / barMax;
                if(prog == 0){
                    mVolumeIcon.setImageResource(R.drawable.ic_volume_off);
                }
                // TODO:tweak these values for prettiness
                else if(prog < 0.3){
                    mVolumeIcon.setImageResource(R.drawable.ic_volume_low);
                }
                else if(prog < 0.8){
                    mVolumeIcon.setImageResource(R.drawable.ic_volume_med);
                }
                else{
                    mVolumeIcon.setImageResource(R.drawable.ic_volume_high);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                changingVolume = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Timer volumeTimer = new Timer();
                volumeTimer.schedule(new TimerTask() {
                    public void run() {
                        runOnUiThread(() -> {
                            changingVolume = false;
                            try{
                                mVolumeBar.setProgress(mPlaybackDevice.getVolume());
                            }catch (ActionNotSupported e) {
                                Log.e(TAG, "Device does not support getting brightness");
                            }});
                    }
                }, 500);
                try {
                    mPlaybackDevice.setVolume(seekBar.getProgress());
                }catch(ActionNotSupported e){
                    Log.e(TAG, "Device does not support setting brightness");
                }
            }
        });

        // Brightness slider
        mBrightnessBar = findViewById(R.id.brightnessBar);
        mBrightnessBar.setMax(barMax);
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
                                mBrightnessBar.setProgress(mPlaybackDevice.getBrightness());
                            }catch (ActionNotSupported e) {
                                Log.e(TAG, "Device does not support getting brightness");
                            }});
                    }
                }, 500);
                try {
                    mPlaybackDevice.setBrightness(seekBar.getProgress());
                }catch(ActionNotSupported e){
                    Log.e(TAG, "Device does not support setting brightness");
                }
            }
        });

        // Play/Pause button
        mPlayPauseButton = findViewById(R.id.mediaPlayPause);
        try{
            currentlyPlaying = mPlaybackDevice.getPlaying();
            if(currentlyPlaying){
                mPlayPauseButton.setImageResource(R.drawable.ic_pause_button);
            }
        }
        catch(ActionNotSupported ans){
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

        /* TODO: Disable the buttons when unusable (use the "disabled" icon resources)
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
            // TODO: Make this clearer. We only want to hide the bars if we can't SET the values
            // TODO: (Although it'd be odd to be able to get and not set).
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
        ControllablePlaybackDevice playbackDevice = (ControllablePlaybackDevice) device;
        if(!changingBrightness){
            try{
                mBrightnessBar.setProgress(playbackDevice.getBrightness());
            }
            catch(ActionNotSupported ans){
                Log.e(TAG, "Device does not support brightness");
            }
        }
        if(!changingVolume){
            try{
                mVolumeBar.setProgress(playbackDevice.getVolume());
            }
            catch(ActionNotSupported ans){
                Log.e(TAG, "Device does not support volume");
            }
        }
    }
}

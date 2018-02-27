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

    private enum buttonMode{enabled,disabled,impossible}

    private buttonMode prevMode = buttonMode.enabled;
    private buttonMode nextMode = buttonMode.enabled;

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
                                Log.e(TAG, "Device does not support getting volume");
                            }});
                    }
                }, 500);
                try {
                    mPlaybackDevice.setVolume(seekBar.getProgress());
                }catch(ActionNotSupported e){
                    Log.e(TAG, "Device does not support setting volume");
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
                 Easiest way to enable this: update the OnClick once we know if the device is
                 Listenable. Current method checks if we can skip
        */
        // Previous button
        mPreviousButton = findViewById(R.id.mediaLeft);
        // Unless we know we're listenable, we can't disable the button.
        mPreviousButton.setOnClickListener((view) -> {
            try{
                mPlaybackDevice.skipPrevious();
            }
            catch(ActionNotSupported actionNotSupported){
                prevMode = buttonMode.impossible;
                mPreviousButton.setImageResource(R.drawable.ic_previous_disabled);
                mPreviousButton.setOnClickListener((v)->{});
            }
        });

        // Next button
        mNextButton = findViewById(R.id.mediaRight);
        mNextButton.setOnClickListener((view) -> {
            try{
                mPlaybackDevice.skipNext();
            }
            catch(ActionNotSupported actionNotSupported){
                nextMode = buttonMode.impossible;
                mNextButton.setImageResource(R.drawable.ic_next_disabled);
                mNextButton.setOnClickListener((v)->{});
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
                // If we know we can update the response, then we can disable the button safely
                mNextButton.setOnClickListener((view) -> {
                    if(nextMode == buttonMode.enabled){
                        try{
                            if(!mPlaybackDevice.skipNext()){
                                nextMode = buttonMode.disabled;
                                mNextButton.setImageResource(R.drawable.ic_next_disabled);
                            }
                            // If we've gone forward and the back button was disabled, we must
                            // now have something to go back to.
                            else if(prevMode == buttonMode.disabled){
                                prevMode = buttonMode.enabled;
                                mPreviousButton.setImageResource(R.drawable.ic_skip_prev);
                            }
                        }
                        catch(ActionNotSupported ans){
                            Log.e(TAG, "PB device does not support forwards skip");
                            nextMode = buttonMode.impossible;
                            mNextButton.setImageResource(R.drawable.ic_next_disabled);
                            mNextButton.setOnClickListener((view2) -> {});
                        }
                    }
                });

                mPreviousButton.setOnClickListener((view) -> {
                    // Unless the button's enabled, don't do anything
                    if(prevMode == buttonMode.enabled){
                        try{
                            // If we can't currently skip to next, disable the button
                            if(!mPlaybackDevice.skipPrevious()){
                                prevMode = buttonMode.disabled;
                                mPreviousButton.setImageResource(R.drawable.ic_previous_disabled);
                            }
                            // Otherwise, assume we can go forward after going back and
                            // re-enable the forward button. Make sure it's not nuked!
                            else if(nextMode == buttonMode.disabled){
                                nextMode = buttonMode.enabled;
                                mNextButton.setImageResource(R.drawable.ic_skip_next);
                            }
                        }
                        catch(ActionNotSupported actionNotSupported){
                            // If we can't support the skip, commence orbital bombardment
                            Log.e(TAG, "PB device does not support backwards skip");
                            prevMode = buttonMode.impossible;
                            mPreviousButton.setImageResource(R.drawable.ic_previous_disabled);
                            mPreviousButton.setOnClickListener((view2) -> {});
                        }
                    }
                });


            }
            else{

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
        ControllablePlaybackDevice playbackDevice = (ControllablePlaybackDevice) device;
        if(!changingBrightness){
            try{
                mBrightnessBar.setProgress(playbackDevice.getBrightness());
            }
            catch(ActionNotSupported ans){
                Log.e(TAG, "PB Device does not support brightness");
            }
        }
        if(!changingVolume){
            try{
                mVolumeBar.setProgress(playbackDevice.getVolume());
            }
            catch(ActionNotSupported ans){
                Log.e(TAG, "PB Device does not support volume");
            }
        }
        try{
            if(playbackDevice.getNext()){

            }
        }
        catch(ActionNotSupported actionNotSupported){
            Log.e(TAG,"PB Device");
        }
    }
}

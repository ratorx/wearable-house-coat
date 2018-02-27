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
import com.clquebec.framework.listenable.PlaybackListener;
import com.clquebec.framework.listenable.Track;
import com.clquebec.framework.storage.ConfigurationStore;

import android.net.Uri;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MediaControlPanelActivity extends WearableActivity implements PlaybackListener {
    private final static String TAG = "MediaControlPanelActivity";
    public static final String ID_EXTRA = "DeviceID";

    private LinearLayout mVolumeWrapper;
    private SeekBar mVolumeBar;
    private ImageView mPreviousButton;
    private ImageView mPlayPauseButton;
    private ImageView mNextButton;
    private ImageView mVolumeIcon;
    private ImageView mAlbumArt;
    private TextView mTrackName;
    private TextView mArtistName;

    private ControllablePlaybackDevice mPlaybackDevice;

    private boolean changingVolume = false;
    private boolean currentlyPlaying = false;
    private int currentVolume = 0;

    private static final int barMax = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_control_panel);

        setAmbientEnabled();

        // Binding UI elements to useful variables
        mVolumeIcon = findViewById(R.id.volumeIcon);
        mVolumeWrapper = findViewById(R.id.volumeControlLayout);
        mAlbumArt = findViewById(R.id.albumArt);
        mTrackName = findViewById(R.id.trackName);
        mArtistName = findViewById(R.id.artistAlbumName);

        // Volume slider
        mVolumeBar = findViewById(R.id.volumeBar);
        mVolumeBar.setMax(barMax);
        mVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
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
                try {
                    currentVolume = seekBar.getProgress();
                    mPlaybackDevice.setVolume(currentVolume);
                }
                catch(ActionNotSupported e){
                    Log.e(TAG, "Device does not support setting volume");
                    return;
                }
                Timer volumeTimer = new Timer();
                volumeTimer.schedule(new TimerTask() {
                    public void run() {
                        runOnUiThread(() -> {
                            changingVolume = false;
                            try{
                                mPlaybackDevice.getVolume(MediaControlPanelActivity.this);
                            }
                            catch(ActionNotSupported actionNotSupported){
                                Log.e(TAG,"Device does not support getting volume");
                            }
                        });
                    }
                }, 500);

            }
        });

        // Play/Pause button
        mPlayPauseButton = findViewById(R.id.mediaPlayPause);

        mPlayPauseButton.setOnClickListener((view) -> {
            if(currentlyPlaying){
                if(mPlaybackDevice.setPlaying(false)){
                    currentlyPlaying = false;
                    mPlayPauseButton.setImageResource(R.drawable.ic_play_button);
                }
                else{
                    Log.d(TAG,"Failed to disable device playback");
                }
            }
            else{
                if(mPlaybackDevice.setPlaying(true)){
                    currentlyPlaying = true;
                    mPlayPauseButton.setImageResource(R.drawable.ic_pause_button);
                }
                else{
                    Log.d(TAG,"Failed to enable device playback");
                }
            }
        });


        // Previous button
        mPreviousButton = findViewById(R.id.mediaLeft);
        mPreviousButton.setOnClickListener((view) -> {
            try{
                mPlaybackDevice.skipPrevious();
            }
            catch(ActionNotSupported actionNotSupported){
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

            // A messy way of checking if we have volume enabled.
            try {
                mPlaybackDevice.getVolume(MediaControlPanelActivity.this);
            }
            catch(ActionNotSupported e){
                Log.e(TAG, "PB device does not support volume get");
                mVolumeWrapper.setVisibility(View.GONE);
            }
        });

        Timer updateScheduler = new Timer();
        updateScheduler.scheduleAtFixedRate(new TimerTask(){
            public void run(){
                mPlaybackDevice.getResource(MediaControlPanelActivity.this);
                mPlaybackDevice.getArtLocation(MediaControlPanelActivity.this);
                try {
                    mPlaybackDevice.getPlaying(MediaControlPanelActivity.this);
                }
                catch(ActionNotSupported actionNotSupported){
                    Log.d(TAG,"PB Device does not support playing status");
                }
                try{
                    mPlaybackDevice.getVolume(MediaControlPanelActivity.this);
                }
                catch(ActionNotSupported actionNotSupported){
                    Log.d(TAG, "PB Device does not support getting volume");
                }
            }
        },0,5000);

    }

    @Override
    public void updateResource(Track resource) {
        mTrackName.setText(resource.trackName);
        mArtistName.setText(resource.artist + " --- " + resource.album);
    }

    @Override
    public void updateIsPlaying(boolean playing) {
        // If the server says we're playing but we don't think we are
        if(playing && !currentlyPlaying){
            currentlyPlaying = true;
            mPlayPauseButton.setImageResource(R.drawable.ic_pause_button);
        }
        else if(!playing && currentlyPlaying){
            currentlyPlaying = false;
            mPlayPauseButton.setImageResource(R.drawable.ic_play_button);
        }
    }

    @Override
    public void updateVolume(int volume) {
        if(!changingVolume){
            mVolumeBar.setProgress(volume);
        }
    }

    @Override
    public void updateArtLocation(String location) {
        mAlbumArt.setImageURI(Uri.parse(location));
    }
}

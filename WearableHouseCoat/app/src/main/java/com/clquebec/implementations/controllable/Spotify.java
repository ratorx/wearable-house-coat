package com.clquebec.implementations.controllable;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.clquebec.framework.HTTPRequestQueue;
import com.clquebec.framework.controllable.ActionNotSupported;
import com.clquebec.framework.controllable.ControllableDeviceType;
import com.clquebec.framework.controllable.ControllablePlaybackDevice;
import com.clquebec.framework.listenable.PlaybackListener;
import com.clquebec.framework.listenable.Track;
import com.clquebec.wearablehousecoat.LightControlPanelActivity;
import com.clquebec.wearablehousecoat.MediaControlPanelActivity;

import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by me on 27/02/2018.
 */

public class Spotify implements ControllablePlaybackDevice {
    public static final String AUTH_TOKEN = "BQCwUuLpiIRyAXPgkDNz_xHrJcMoyOl98wegmTltlVjK_gGqzxvA9FX8dyy8W61LJkXPDW0ObvSigTbVuREVP8j5COquvBKgb8-R_iG2194AnaCxs2r4xtpl-tKjsrA7Lg9dQEFpjfONBm-wVAjrBIAJQw";
    private boolean isPlaying = false;
    private Context mContext;
    private UUID mUUID;

    public Spotify(Context c){
        mContext = c;
    }

    public Spotify(Context c, UUID id, JSONObject config){
        mContext = c;
        mUUID = id;
    }

    @Override
    public void getResource(PlaybackListener pl) {
        pl.updateResource(new Track("blobby", "Mr Blobby", "blobby blobby blobby blobby blobby blobby blobby blobby"));
    }

    @Override
    public boolean skipNext() throws ActionNotSupported {
        Log.d("Spotify", "Running get next");
        SpotifyJsonRequest skip = new SpotifyJsonRequest(JsonObjectRequest.Method.POST, "https://api.spotify.com/v1/me/player/next", null,
                response -> Log.d("Spotify", "Response is " + response.toString()),
                error -> Log.d("Spotify", "Error is " + error.getMessage())){

        };
        HTTPRequestQueue.getRequestQueue(mContext).addToRequestQueue(skip);

        return true;
    }

    @Override
    public boolean skipPrevious() throws ActionNotSupported {
        return false;
    }

    @Override
    public boolean setPlaying(boolean enabled) {
        if (enabled){
            SpotifyJsonRequest play = new SpotifyJsonRequest(JsonObjectRequest.Method.PUT, "https://api.spotify.com/v1/me/player/play", null,
                    response -> Log.d("Spotify", "Response is " + response.toString()),
                    error -> Log.d("Spotify", "Error is " + error.getMessage())){

            };
            HTTPRequestQueue.getRequestQueue(mContext).addToRequestQueue(play);

        }else{
            SpotifyJsonRequest pause = new SpotifyJsonRequest(JsonObjectRequest.Method.PUT, "https://api.spotify.com/v1/me/player/pause", null,
                    response -> Log.d("Spotify", "Response is " + response.toString()),
                    error -> Log.d("Spotify", "Error is " + error.getMessage())){
            };
            HTTPRequestQueue.getRequestQueue(mContext).addToRequestQueue(pause);
        }

        return true;
    }

    @Override
    public void getPlaying(PlaybackListener pl) throws ActionNotSupported {

    }

    @Override
    public boolean setVolume(int volume) throws ActionNotSupported {
        return false;
    }

    @Override
    public void getVolume(PlaybackListener pl) throws ActionNotSupported {

    }

    @Override
    public void getArtLocation(PlaybackListener pl) {
    }

    @Override
    public boolean enable() {
        return false;
    }

    @Override
    public boolean disable() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return isPlaying;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getName() {
        return "Spotify Controller";
    }

    @Override
    public ControllableDeviceType getType() {
        return ControllableDeviceType.SOUND;
    }

    @Override
    public UUID getID() {
        return mUUID;
    }

    @Override
    public boolean quickAction() {
        isPlaying = !isPlaying;
        setPlaying(isPlaying);
        return true;
    }

    @Override
    public boolean extendedAction() {
        Intent soundControls = new Intent(mContext, MediaControlPanelActivity.class);
        soundControls.putExtra(MediaControlPanelActivity.ID_EXTRA, this.getID());
        mContext.startActivity(soundControls);

        return true;
    }

    @Override
    public boolean isConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

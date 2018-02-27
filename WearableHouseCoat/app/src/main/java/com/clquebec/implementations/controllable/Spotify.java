package com.clquebec.implementations.controllable;

import android.content.Context;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.clquebec.framework.HTTPRequestQueue;
import com.clquebec.framework.controllable.ActionNotSupported;
import com.clquebec.framework.controllable.ControllableDeviceType;
import com.clquebec.framework.controllable.ControllablePlaybackDevice;
import com.clquebec.framework.listenable.PlaybackListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by me on 27/02/2018.
 */

public class Spotify implements ControllablePlaybackDevice {
    private static final String AUTH_TOKEN = "BQBv_EtwurPlg7j13PTPgzvuFlKtubN3lUlLUNF-YRG3rQ-B4cAkG8wRk1GYAIs589oXNSjaiwjSom-YuY78uUIlOGMSgOK1f34_epigRpFqszR5DB9mG-ZaZ-tdZExtt8hS-j9ScPXnUMfymoAExaO4Fg";
    private boolean isPlaying;
    private Context mContext;

    public Spotify(Context c){
        mContext = c;
    }

    @Override
    public void getResource(PlaybackListener pl) {

    }

    @Override
    public boolean skipNext() throws ActionNotSupported {
        return false;
    }

    @Override
    public boolean skipPrevious() throws ActionNotSupported {
        return false;
    }

    @Override
    public boolean setPlaying(boolean enabled) {
        if (enabled){
            JsonObjectRequest play = new JsonObjectRequest(JsonObjectRequest.Method.PUT, "https://api.spotify.com/v1/me/player/play", null,
                    response -> Log.d("Spotify", "Response is " + response.toString()),
                    error -> Log.d("Spotify", "Error is " + error.getMessage())){
                @Override
                public Map<String, String> getHeaders(){
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + AUTH_TOKEN);
                    return headers;
                }
            };
            HTTPRequestQueue.getRequestQueue(mContext).addToRequestQueue(play);

        }else{
            JsonObjectRequest pause = new JsonObjectRequest(JsonObjectRequest.Method.PUT, "https://api.spotify.com/v1/me/player/pause", null,
                    response -> Log.d("Spotify", "Response is " + response.toString()),
                    error -> Log.d("Spotify", "Error is " + error.getMessage())){
                @Override
                public Map<String, String> getHeaders(){
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + AUTH_TOKEN);
                    return headers;
                }
            };
            HTTPRequestQueue.getRequestQueue(mContext).addToRequestQueue(pause);
        }

        return true;
    }


    @Override
    public void getPlaying(PlaybackListener pl) throws ActionNotSupported {

    }

    @Override
    public boolean setVolume(float volume) throws ActionNotSupported {
        return false;
    }

    @Override
    public void getVolume(PlaybackListener pl) throws ActionNotSupported {

    }

    @Override
    public String getArtLocation(PlaybackListener pl) {
        return null;
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
        return false;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ControllableDeviceType getType() {
        return null;
    }

    @Override
    public UUID getID() {
        return null;
    }

    @Override
    public boolean quickAction() {
        return false;
    }

    @Override
    public boolean extendedAction() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return false;
    }
}

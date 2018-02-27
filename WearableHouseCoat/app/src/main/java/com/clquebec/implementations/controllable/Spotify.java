package com.clquebec.implementations.controllable;

import android.content.Context;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.clquebec.framework.HTTPRequestQueue;
import com.clquebec.framework.controllable.ActionNotSupported;
import com.clquebec.framework.controllable.ControllableDeviceType;
import com.clquebec.framework.controllable.ControllablePlaybackDevice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by me on 27/02/2018.
 */

public class Spotify implements ControllablePlaybackDevice {
    private static final String AUTH_TOKEN = "BQBv_EtwurPlg7j13PTPgzvuFlKtubN3lUlLUNF-YRG3rQ-B4cAkG8wRk1GYAIs589oXNSjaiwjSom-YuY78uUIlOGMSgOK1f34_epigRpFqszR5DB9mG-ZaZ-tdZExtt8hS-j9ScPXnUMfymoAExaO4Fg";


    public Spotify(Context c){
        JsonObjectRequest getDevices = new JsonObjectRequest(JsonObjectRequest.Method.GET, "https://api.spotify.com/v1/me/player/devices", null,
                        response -> Log.d("Spotify", response.toString()),
                        error -> Log.d("Spotify", error.getMessage())){
            @Override
            public Map<String, String> getHeaders(){
               Map<String, String> headers = new HashMap<>();
               headers.put("Accept", "application/json");
               headers.put("Content-Type", "application/json");
               headers.put("Authorization", "Bearer " + AUTH_TOKEN);
               return headers;
            }
        };
        HTTPRequestQueue.getRequestQueue(c).addToRequestQueue(getDevices);
    }


    @Override
    public void setResource(String resource) {

    }

    @Override
    public String getResource() {
        return null;
    }

    @Override
    public boolean setVolume(float volume) throws ActionNotSupported {
        return false;
    }

    @Override
    public int getVolume() throws ActionNotSupported {
        return 0;
    }

    @Override
    public boolean setBrightness(float brightness) throws ActionNotSupported {
        return false;
    }

    @Override
    public int getBrightness() throws ActionNotSupported {
        return 0;
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

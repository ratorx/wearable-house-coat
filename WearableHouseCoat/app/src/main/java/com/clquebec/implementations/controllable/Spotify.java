package com.clquebec.implementations.controllable;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.clquebec.framework.HTTPRequestQueue;
import com.clquebec.framework.controllable.ActionNotSupported;
import com.clquebec.framework.controllable.ControllableDeviceType;
import com.clquebec.framework.controllable.ControllablePlaybackDevice;
import com.clquebec.framework.listenable.DeviceChangeListener;
import com.clquebec.framework.listenable.ListenableDevice;
import com.clquebec.framework.listenable.PlaybackListener;
import com.clquebec.framework.listenable.Track;
import com.clquebec.wearablehousecoat.MediaControlPanelActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by me on 27/02/2018.
 */

public class Spotify implements ControllablePlaybackDevice, ListenableDevice {
    public static final String AUTH_TOKEN = "BQAdqgyFDpIpOvkKmvvYPzk3n_7rnNDgMStXpyWmPHdUQ5cmaqHZfQcV7c0pj8qENAdx4raaHuozAdVoQ9tpzkWxCk8xFAOlYnL50eemZuqndFBD3rv5aEv6bxzKJWv_ypTvvo7zXeYWDyEGwHqjdXf2oQ";
    private boolean isPlaying = false;
    private Context mContext;
    private UUID mUUID;
    private List<DeviceChangeListener> listeners = new ArrayList<>();
    private String mName = "Spotify";


    public Spotify(Context c, UUID id, JSONObject config){
        mContext = c;
        mUUID = id;
        try{
            getPlaying(null);
        }catch (ActionNotSupported e) {
            Log.e("Spotify", e.getMessage());
        }
    }

    public void getAll(PlaybackListener pl) {
        sendSpotifyRequest(JsonObjectRequest.Method.GET,
                "https://api.spotify.com/v1/me/player",
                response -> {
                    Log.d("Spotify", "Response is " + response.toString());
                    String track = "";
                    String artist = "";
                    String album = "";
                    boolean playing = false;
                    int volume = 0;
                    String url = "";
                    try{
                        track = response.getJSONObject("item").getString("name");
                        album = response.getJSONObject("item").getJSONObject("album").getString("name");
                        artist = response.getJSONObject("item").getJSONObject("album").getJSONArray("artists").getJSONObject(0).getString("name");
                        playing = response.getBoolean("is_playing");
                        volume = response.getJSONObject("device").getInt("volume_percent");
                        url = response.getJSONObject("item").getJSONObject("album").getJSONArray("images").getJSONObject(2).getString("url");
                    } catch (JSONException e){
                        Log.e("Spotify", e.getMessage());
                    }
                    isPlaying=playing;
                    updateListeners();
                    if (pl != null) {
                        pl.updateResource(new Track(track, artist, album));
                        pl.updateIsPlaying(playing);
                        pl.updateVolume(volume);
                        pl.updateArtLocation(url);
                    }
                }
                ,
                null);
    }

    @Override
    public void getResource(PlaybackListener pl) {
        sendSpotifyRequest(JsonObjectRequest.Method.GET,
                "https://api.spotify.com/v1/me/player",
                response -> {
                    Log.d("Spotify", "Response is " + response.toString());
                    String track = "";
                    String artist = "";
                    String album = "";
                    try{
                        track = response.getJSONObject("item").getString("name");
                        album = response.getJSONObject("item").getJSONObject("album").getString("name");
                        artist = response.getJSONObject("item").getJSONObject("album").getJSONArray("artists").getJSONObject(0).getString("name");
                    } catch (JSONException e){
                        Log.e("Spotify", e.getMessage());
                    }
                    if (pl != null) {
                        pl.updateResource(new Track(track, artist, album));
                    }
                }
                ,
                null);
    }

    @Override
    public boolean skipNext() throws ActionNotSupported {
        sendSpotifyRequest(JsonObjectRequest.Method.POST, "https://api.spotify.com/v1/me/player/next");

        return true;
    }

    @Override
    public boolean skipPrevious() throws ActionNotSupported {
        sendSpotifyRequest(JsonObjectRequest.Method.POST, "https://api.spotify.com/v1/me/player/previous");

        return true;
    }

    @Override
    public boolean setPlaying(boolean enabled) {
        if (enabled){
            sendSpotifyRequest(JsonObjectRequest.Method.PUT, "https://api.spotify.com/v1/me/player/play");

        }else{
            sendSpotifyRequest(JsonObjectRequest.Method.PUT, "https://api.spotify.com/v1/me/player/pause");
        }

        return true;
    }

    @Override
    public void getPlaying(PlaybackListener pl) throws ActionNotSupported {
       sendSpotifyRequest(JsonObjectRequest.Method.GET,
                "https://api.spotify.com/v1/me/player",
                response -> {
                    Log.d("Spotify", "Response is " + response.toString());
                    boolean playing = false;
                    try{
                        playing = response.getBoolean("is_playing");
                    } catch (JSONException e){
                        Log.e("Spotify", e.getMessage());
                    }
                    isPlaying=playing;
                    updateListeners();
                    if (pl != null) {
                        pl.updateIsPlaying(playing);
                    }
                }
                ,
                null);
    }

    @Override
    public boolean setVolume(int volume) throws ActionNotSupported {
        sendSpotifyRequest(JsonObjectRequest.Method.PUT, "https://api.spotify.com/v1/me/player/volume" +
                "?volume_percent=" + volume);

        return true;
    }

    @Override
    public void getVolume(PlaybackListener pl) throws ActionNotSupported {
        sendSpotifyRequest(JsonObjectRequest.Method.GET,
                "https://api.spotify.com/v1/me/player",
                response -> {
                    Log.d("Spotify", "Response is " + response.toString());
                    int volume = 0;
                    try{
                        volume = response.getJSONObject("device").getInt("volume_percent");
                    } catch (JSONException e){
                        Log.e("Spotify", e.getMessage());
                    }
                    if (pl != null) {
                        pl.updateVolume(volume);
                    }
                }
                ,
                null);
    }

    @Override
    public void getArtLocation(PlaybackListener pl) {
        sendSpotifyRequest(JsonObjectRequest.Method.GET,
                "https://api.spotify.com/v1/me/player",
                response -> {
                    Log.d("Spotify", "Response is " + response.toString());
                    String url = "";
                    try{
                        url = response.getJSONObject("item").getJSONObject("album").getJSONArray("images").getJSONObject(2).getString("url");
                        Log.d("Spotify", url);
                    } catch (JSONException e){
                        Log.e("Spotify", e.getMessage());
                    }
                    if (pl != null) {
                        pl.updateArtLocation(url);
                    }
                }
                ,
                null);
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
        mName = name;
    }

    @Override
    public String getName() {
        return mName;
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

    @Override
    public void addListener(DeviceChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(DeviceChangeListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        for (DeviceChangeListener l : listeners) {
            l.updateState(this);
        }
    }

    private void sendSpotifyRequest(int method, String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        if (listener==null){
            listener = response -> Log.d("Spotify", "Response is " + response.toString());
        }
        if (errorListener==null){
            errorListener = error -> Log.d("Spotify", "Error is " + error.getMessage());
        }

        SpotifyJsonRequest request = new SpotifyJsonRequest(method,
                url, null,
                listener,
                errorListener){
        };
        HTTPRequestQueue.getRequestQueue(mContext).addToRequestQueue(request);
    }

    private void sendSpotifyRequest(int method, String url) {
        Response.Listener<JSONObject> listener = response -> Log.d("Spotify", "Response is " + response.toString());

        Response.ErrorListener errorListener = error -> Log.d("Spotify", "Error is " + error.getMessage());

        SpotifyJsonRequest request = new SpotifyJsonRequest(method,
                url, null,
                listener,
                errorListener){
        };
        HTTPRequestQueue.getRequestQueue(mContext).addToRequestQueue(request);
    }
}

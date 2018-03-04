package com.clquebec.implementations.controllable;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.clquebec.environment.Keys;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by me on 27/02/2018.
 */

public class Spotify implements ControllablePlaybackDevice, ListenableDevice {
    private static final String TAG = "Spotify";
    public static final String redirect_uri= "blah";
    private boolean isPlaying = false;
    private Context mContext;
    private UUID mUUID;
    private List<DeviceChangeListener> listeners = new ArrayList<>();
    private String mName = "Spotify";

    private String mAuthCode;
    private String mAccessToken = null;
    private String mRefreshToken = null;
    private long mExpiryTime;

    public class SpotifyJsonRequest extends JsonObjectRequest {
        public SpotifyJsonRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        public SpotifyJsonRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(url, jsonRequest, listener, errorListener);
        }

        @Override
        public Map<String, String> getHeaders(){
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + mAccessToken);
            return headers;
        }
    }


    public Spotify(Context c, UUID id, JSONObject config) throws JSONException{
        mContext = c;
        mUUID = id;
        try{
            getPlaying(null);
        }catch (ActionNotSupported e) {
            Log.e(TAG, e.getMessage());
        }

        //Get auth token if available
        mAuthCode = config.getString("authkey");

        getInitialTokens();
    }

    public void getAll(PlaybackListener pl) {
        sendSpotifyRequest(JsonObjectRequest.Method.GET,
                response -> {
                    Log.d(TAG, "Response is " + response.toString());
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
                        Log.e(TAG, e.getMessage());
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
                response -> {
                    Log.d(TAG, "Response is " + response.toString());
                    String track = "";
                    String artist = "";
                    String album = "";
                    try{
                        track = response.getJSONObject("item").getString("name");
                        album = response.getJSONObject("item").getJSONObject("album").getString("name");
                        artist = response.getJSONObject("item").getJSONObject("album").getJSONArray("artists").getJSONObject(0).getString("name");
                    } catch (JSONException e){
                        Log.e(TAG, e.getMessage());
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
    public boolean setPlaying(Boolean enabled) {
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
               response -> {
                    Log.d(TAG, "Response is " + response.toString());
                    boolean playing = false;
                    try{
                        playing = response.getBoolean("is_playing");
                    } catch (JSONException e){
                        Log.e(TAG, e.getMessage());
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
    public boolean setVolume(Integer volume) throws ActionNotSupported {
        sendSpotifyRequest(JsonObjectRequest.Method.PUT, "https://api.spotify.com/v1/me/player/volume" +
                "?volume_percent=" + volume);

        return true;
    }

    @Override
    public void getVolume(PlaybackListener pl) throws ActionNotSupported {
        sendSpotifyRequest(JsonObjectRequest.Method.GET,
                response -> {
                    Log.d(TAG, "Response is " + response.toString());
                    int volume = 0;
                    try{
                        volume = response.getJSONObject("device").getInt("volume_percent");
                    } catch (JSONException e){
                        Log.e(TAG, e.getMessage());
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
                response -> {
                    Log.d(TAG, "Response is " + response.toString());
                    String url = "";
                    try{
                        url = response.getJSONObject("item").getJSONObject("album").getJSONArray("images").getJSONObject(2).getString("url");
                        Log.d(TAG, url);
                    } catch (JSONException e){
                        Log.e(TAG, e.getMessage());
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

    private void sendSpotifyRequest(int method, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        if (listener==null){
            listener = response -> Log.d(TAG, "Response is " + response.toString());
        }
        if (errorListener==null){
            errorListener = error -> Log.d(TAG, "Error is " + error.getMessage());
        }

        SpotifyJsonRequest request = new SpotifyJsonRequest(method,
                "https://api.spotify.com/v1/me/player", null,
                listener,
                errorListener){
        };
        HTTPRequestQueue.getRequestQueue(mContext).addToRequestQueue(request);
    }

    private void sendSpotifyRequest(int method, String url) {
        Response.Listener<JSONObject> listener = response -> Log.d("Spotify", "Response is " + response.toString());

        Response.ErrorListener errorListener = error -> Log.e("Spotify", "Error code: "
                + error.networkResponse.statusCode);

        SpotifyJsonRequest request = new SpotifyJsonRequest(method,
                url, null,
                listener,
                errorListener){
        };
        HTTPRequestQueue.getRequestQueue(mContext).addToRequestQueue(request);
    }

    private String getEncodedClientCredentials() {
        byte[] bytesDecoded = (Keys.SPOTIFY_CLIENTID + ":" + Keys.SPOTIFY_CLIENT_SECRET).getBytes();
        byte[] bytesEncoded = Base64.encode(bytesDecoded, Base64.DEFAULT);

        return new String(bytesEncoded);
    }

    private void getInitialTokens(){
        String clientCredentials = getEncodedClientCredentials();

        Response.Listener<JSONObject> listener = (response -> {
            Log.d(TAG, "Response is " + response.toString());
            try {
                mAccessToken = response.getString("access_token");
                mRefreshToken = response.getString("refresh_token");
                mExpiryTime = (System.currentTimeMillis() / 1000) + (long)
                        (response.getInt("expires_in") * 0.9);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        Response.ErrorListener errorListener = error -> Log.e(TAG, "Error code: " +
                error.networkResponse.statusCode);

        JSONObject authorisationBody = new JSONObject();
        try {
            authorisationBody.put("grant_type", "authorization_code");
            authorisationBody.put("code", mAuthCode);
            authorisationBody.put("redirect_uri", redirect_uri);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST,
                "https://accounts.spotify.com/api/token", authorisationBody, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + clientCredentials);
                return headers;
            }
        };

        HTTPRequestQueue.getRequestQueue(mContext).addToRequestQueue(jsonObjectRequest);
    }

}

package clquebec.com.implementations.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import clquebec.com.framework.HTTPRequestQueue;
import clquebec.com.framework.location.IndoorLocationProvider;
import clquebec.com.framework.location.LocationChangeListener;
import clquebec.com.framework.location.Place;
import clquebec.com.framework.location.Room;
import clquebec.com.framework.people.Person;

/*
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 08/02/18
 */

public class FINDLocationProvider implements IndoorLocationProvider {
    private static final String TAG = "FIND";

    public static final String GROUPID = "LULLINGLABRADOODLE";
    public static final String SERVERURL = "http://shell.srcf.net:8003/";
    public static final int POLLDELAYMILLIS = 5000;

    private Context mContext;
    private LocationChangeListener mListener;
    private HTTPRequestQueue mQueue; //For making HTTP requests

    private interface FingerprintCallback {
        void onFingerprint(JSONArray fingerprint);
    }

    private Person mPerson; // Use for calibration and update
    private Map<Person, Place> mLocationMap;

    public FINDLocationProvider(Context c, Person p) {
        mQueue = HTTPRequestQueue.getRequestQueue(c);
        mLocationMap = new HashMap<>();
        mContext = c;
        mPerson = p;
    }

    @Nullable
    @Override
    public Place getLocation(Person p) {
        return mLocationMap.getOrDefault(p, null);
    }

    @Override
    public void refreshLocations() {
        String url = SERVERURL + "location?group=" + GROUPID;

        JsonObjectRequest locationRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "Successfully Received Locations");

                    try {
                        //Iterate over response
                        JSONObject users = response.getJSONObject("users");
                        Iterator<String> keys = users.keys();
                        while (keys.hasNext()) {
                            String user = keys.next();

                            //Get data
                            JSONArray userData = users.getJSONArray(user);
                            Person person = Person.getPerson(UUID.fromString(user));
                            Place location = new Room(mContext, userData.getJSONObject(0).getString("location"));

                            //Update internal structure
                            mLocationMap.put(person, location);
                            person.setLocation(location);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Cannot extract JSON");
                    }
                },
                error -> Log.e(TAG, "Failed to get locations, " + error.getMessage())
        );

        mQueue.addToRequestQueue(locationRequest);
    }

    private void getFingerprint(FingerprintCallback callback) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        if(wifiManager == null){
            Log.e(TAG, "WiFi is not available");
            return;
        }

        if (!wifiManager.isWifiEnabled()) { // TODO: Deal with potential NullPointerException
            //Forcefully enable wifi
            wifiManager.setWifiEnabled(true);
        }

        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                if (intent.getExtras() != null && intent.getExtras().getBoolean(WifiManager.EXTRA_RESULTS_UPDATED)) {
                    List<ScanResult> results = wifiManager.getScanResults();

                    //Generate fingerprint JSONArray
                    JSONArray fingerprint = new JSONArray();
                    for (ScanResult result : results) {
                        try {
                            JSONObject AP = new JSONObject();

                            AP.put("mac", result.BSSID);
                            AP.put("rssi", result.level);

                            fingerprint.put(AP);
                        } catch (JSONException e) {
                            Log.e(TAG, "Could not add information about AP to fingerprint, " + e.getMessage());
                        }
                    }

                    //Call private method to send update
                    callback.onFingerprint(fingerprint);
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();
    }

    @Override
    public boolean update() {
        getFingerprint(fingerprint -> {
            String url = SERVERURL + "track";

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("group", GROUPID);
                jsonObject.put("username", mPerson.getName());
                //jsonObject.put("location", getCurrentLocation(mPerson));
                jsonObject.put("time", System.currentTimeMillis() / 1000); //TODO: time zones
                jsonObject.put("wifi-fingerprint", fingerprint);
            } catch (JSONException e) {
                Log.e(TAG, "Could not generate update request, " + e.getMessage());
            }

            JsonObjectRequest trackRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> Log.d(TAG, "Succesfully updated fingerprint"),
                    error -> Log.e(TAG, error.getMessage()));

            mQueue.addToRequestQueue(trackRequest);
        });
        return true;
    }

    @Override
    public boolean calibrate(Place room) {
        getFingerprint(fingerprint -> {
            String url = SERVERURL + "learn";

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("group", GROUPID);
                jsonObject.put("username", mPerson.getName());
                jsonObject.put("location", room.getName());
                jsonObject.put("time", System.currentTimeMillis() / 1000); //TODO: time zones
                jsonObject.put("wifi-fingerprint", fingerprint);
            } catch (JSONException e) {
                Log.e(TAG, "Could not generate update request, " + e.getMessage());
            }

            Log.d(TAG, jsonObject.toString());
            JsonObjectRequest trackRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> Log.d(TAG, "Succesfully calibrated"),
                    error -> Log.e(TAG, error.getMessage()));

            mQueue.addToRequestQueue(trackRequest);
        });
        return true;
    }

    @Override
    public boolean reset() {
        String url = SERVERURL + "database?group=" + GROUPID;
        StringRequest newRequest = new StringRequest(Request.Method.DELETE, url,
                response -> Log.d(TAG, "Deleted group"),
                error -> Log.e(TAG, error.getMessage())
        );
        mQueue.addToRequestQueue(newRequest);
        return true;
    }
}

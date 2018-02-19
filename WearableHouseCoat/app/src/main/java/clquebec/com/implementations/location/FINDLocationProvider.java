package clquebec.com.implementations.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import clquebec.com.framework.location.LocationCalibrator;
import clquebec.com.framework.location.LocationChangeListener;
import clquebec.com.framework.location.LocationGetter;
import clquebec.com.framework.location.LocationUpdater;
import clquebec.com.framework.location.Place;
import clquebec.com.framework.location.Room;
import clquebec.com.framework.people.Person;

/*
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 08/02/18
 */

public class FINDLocationProvider implements LocationGetter, LocationCalibrator, LocationUpdater {
    public static final String GROUPID = "LULLINGLABRADOODLE";
    public static final String SERVERURL = "http://shell.srcf.net:8003/";
    public static final int POLLDELAYMILLIS = 5000;

    private Context mContext;
    private LocationChangeListener mListener;
    private RequestQueue mQueue; //For making HTTP requests

    private interface FingerprintCallback {
        void onFingerprint(JSONArray fingerprint);
    }

    private Person mPerson; // Use for calibration and update
    private Map<Person, Place> mLocationMap;

    private Handler mLocationUpdateHandler = new Handler();

    public FINDLocationProvider(Context c, Person p) {
        mQueue = Volley.newRequestQueue(c);
        mLocationMap = new HashMap<>();
        mContext = c;
        mPerson = p;

        Runnable mLocationUpdater = new Runnable() {
            @Override
            public void run() {
                forceLocationRefresh();
                update();
                mLocationUpdateHandler.postDelayed(this, POLLDELAYMILLIS);
            }
        };
        mLocationUpdateHandler.post(mLocationUpdater);
    }

    @Nullable
    @Override
    public Place getCurrentLocation(Person p) {
        return mLocationMap.getOrDefault(p, null);
    }

    @Override
    public void setLocationChangeListener(@Nullable LocationChangeListener listener) {
        mListener = listener;

        if (listener != null) {
            for (Person p : mLocationMap.keySet()) {
                listener.onLocationChanged(p, null, mLocationMap.get(p));
            }
        }
    }

    @Override
    public void forceLocationRefresh() {
        String url = SERVERURL + "location?group=" + GROUPID;

        JsonObjectRequest locationRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    Log.d("FIND", "Successfully Received Locations");

                    try {
                        //Iterate over response
                        JSONObject users = response.getJSONObject("users");
                        Iterator<String> keys = users.keys();
                        while (keys.hasNext()) {
                            String user = keys.next();

                            //Get data
                            JSONArray userData = users.getJSONArray(user);
                            Person person = new Person(user);
                            Place location = new Room(mContext, userData.getJSONObject(0).getString("location"));

                            //Notify change
                            if (mListener != null) {
                                if (!mLocationMap.containsKey(person) || !mLocationMap.get(person).equals(location)) {
                                    mListener.onLocationChanged(
                                            person,
                                            mLocationMap.getOrDefault(person, null),
                                            location
                                    );
                                }
                            }

                            //Update internal structure
                            mLocationMap.put(person, location);
                        }
                    } catch (JSONException e) {
                        Log.e("FIND", "Cannot extract JSON");
                    }
                },
                error -> Log.e("FIND", "Failed to get locations, " + error.getMessage())
        );

        mQueue.add(locationRequest);
    }

    private void getFingerprint(FingerprintCallback callback) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        if(wifiManager == null){
            Log.e("FIND", "WiFi is not available");
            return;
        }

        if (!wifiManager.isWifiEnabled()) {
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
                            Log.e("FIND", "Could not add information about AP to fingerprint, " + e.getMessage());
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
                Log.e("FIND", "Could not generate update request, " + e.getMessage());
            }

            JsonObjectRequest trackRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> Log.d("FIND", "Successfully calibrated"),
                    error -> Log.e("FIND", error.getMessage()));

            mQueue.add(trackRequest);
        });
        return true;
    }

    @Override
    public boolean calibrate(Room room) {
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
                Log.e("FIND", "Could not generate update request, " + e.getMessage());
            }

            JsonObjectRequest trackRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> Log.d("FIND", "Successfully updated fingerprint"),
                    error -> Log.e("FIND", error.getMessage()));

            mQueue.add(trackRequest);
        });
        return true;
    }

    @Override
    public boolean calibrate(Room room, String data) {
        //Discard extra data - for now
        return calibrate(room);
    }

    @Override
    public boolean calibrate(Room room, JSONObject data) {
        //Discard extra data - for now
        return calibrate(room);
    }

    @Override
    public boolean reset() {
        String url = SERVERURL + "database?group=" + GROUPID;
        StringRequest newRequest = new StringRequest(Request.Method.DELETE, url,
                response -> Log.d("FIND", "Deleted group"),
                error -> Log.e("FIND", error.getMessage())
        );
        mQueue.add(newRequest);
        return true;
    }
}

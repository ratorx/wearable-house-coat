package clquebec.com.implementations.location;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import clquebec.com.framework.location.IndoorLocationProvider;
import clquebec.com.framework.location.LocationChangeListener;
import clquebec.com.framework.location.Place;
import clquebec.com.framework.location.Room;
import clquebec.com.framework.people.Person;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 08/02/18
 */

public class FINDLocationProvider implements IndoorLocationProvider {
    public static final String GROUPID = "LULLINGLABRADOODLE";
    public static final String SERVERURL = "https://shell.srcf.net:8003/";

    private Context mContext;
    private WifiManager mWifiManager;
    private LocationChangeListener mListener;
    private RequestQueue mQueue; //For making HTTP requests

    private Map<Person, Place> mLocationMap;

    private List<ScanResult> getWiFiScan(){
        //TODO: extract into seperate class
        mWifiManager.startScan();
        List<ScanResult> wifiList = mWifiManager.getScanResults();
        return wifiList;
    }

    public FINDLocationProvider(Context c){
        mWifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        mQueue = Volley.newRequestQueue(c);
        mLocationMap = new HashMap<>();
        mContext = c;

        //TODO: Spin up a background thread, to do the following on repeat
        String url = SERVERURL+"location?group="+GROUPID;
        JsonObjectRequest locationRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    Log.d("FIND", "Succesfully Received Locations\n" + response.toString());

                    try {
                        //Iterate over response
                        JSONObject users = response.getJSONObject("users");
                        Iterator<String> keys = users.keys();
                        while(keys.hasNext()){
                            String user = keys.next();

                            //Get data
                            JSONArray userData = users.getJSONArray(user);
                            Person person = new Person(user);
                            Place location = new Room(c, userData.getJSONObject(0).getString("location"));

                            //Notify change
                            if(mListener != null){
                                if(!mLocationMap.containsKey(person) || !mLocationMap.get(person).equals(location)) {
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
                    }catch(JSONException e){
                        Log.e("FIND", "Cannot extract JSON");
                    }
                },
                error -> Log.e("FIND", "Failed to get locations, "+ error.getMessage())
        );

        mQueue.add(locationRequest);
    }

    @Nullable
    @Override
    public Place getCurrentLocation(Person p) {
        return null;
    }

    @Override
    public void setLocationChangeListener(@Nullable LocationChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void forceLocationRefresh() {

    }
}

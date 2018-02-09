package clquebec.com.implementations.location;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;

import com.android.volley.RequestQueue;

import java.util.List;

import clquebec.com.framework.location.IndoorLocationProvider;
import clquebec.com.framework.location.LocationChangeListener;
import clquebec.com.framework.location.Place;
import clquebec.com.framework.people.Person;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 08/02/18
 */

public class FINDLocationProvider implements IndoorLocationProvider {
    private WifiManager mWifiManager;
    private RequestQueue mQueue; //For making HTTP requests

    private List<ScanResult> getWiFiScan(){
        mWifiManager.startScan();
        List<ScanResult> wifiList = mWifiManager.getScanResults();
        return wifiList;
    }

    public FINDLocationProvider(Context c){
        //Spin up a background thread

        mWifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);


    }

    @Nullable
    @Override
    public Place getCurrentLocation(Person p) {
        return null;
    }

    @Override
    public void setLocationChangeListener(@Nullable LocationChangeListener listener) {

    }
}

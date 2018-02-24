package com.clquebec.framework.automations;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.clquebec.framework.HTTPRequestQueue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 24/02/18
 */

public class GetInstanceId extends FirebaseInstanceIdService {
    private static String TAG = "GetInstanceId";
    private static String SERVER = "http://shell.srcf.net:8003/";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        HTTPRequestQueue.getRequestQueue(this).addToRequestQueue(new StringRequest(Request.Method.GET, SERVER,
                response -> Log.d(TAG, response),
                error -> Log.e(TAG, error.getMessage())
        ));
    }
}

package com.clquebec.framework;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by TB on 2/15/2018.
 */

public class HTTPRequestQueue {

    private static HTTPRequestQueue mRequestQueue;
    private static RequestQueue mQueue;

    private HTTPRequestQueue(Context context) {
        mQueue = Volley.newRequestQueue(context);
    }

    public static HTTPRequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) mRequestQueue = new HTTPRequestQueue(context);
        return mRequestQueue;
    }

    public void addToRequestQueue(JsonObjectRequest request) {
        mQueue.add(request);
    }

    public void addToRequestQueue(StringRequest request) {
        mQueue.add(request);
    }

}

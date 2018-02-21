package clquebec.com.framework;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import clquebec.com.environment.Keys;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class IFTTT {
    private static final String TAG = "IFTTT";

    private String mMakerKey;
    private HTTPRequestQueue mQueue;

    private static IFTTT mInstance = null;

    private IFTTT(Context context) {
        mQueue = HTTPRequestQueue.getRequestQueue(context);

        //TODO: Replace makerkey with a user-provided one
        mMakerKey = Keys.IFTTT;
    }

    public static IFTTT getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new IFTTT(context);
        }

        return mInstance;
    }

    public void webhook(String event) {
        webhook(event, new ArrayList<>());
    }

    public void webhook(String event, final List<String> values) {
        //Takes the first 3 values and sends them

        if (mMakerKey == null || event == null) return;

        String url = "https://maker.ifttt.com/trigger/" + event + "/with/key/" + mMakerKey;

        final JSONObject json = new JSONObject();
        if (values != null && values.size() > 0) {
            //Collect values into JSON
            for (int i = 0; i < values.size(); i++) {
                try {
                    json.put("values" + (i + 1), values.get(i));
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException on value" + (i + 1));
                }
            }
        }

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    Log.d(TAG, "Response: " + response);
                },
                error -> {
                    // error
                    Log.d(TAG, "IFTTT Error: " + error.getMessage());
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return json.toString().getBytes();
            }
        };

        mQueue.addToRequestQueue(postRequest);
    }
}

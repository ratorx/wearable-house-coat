package clquebec.com.framework;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clquebec.com.environment.Keys;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class IFTTT {
    private String mMakerKey = Keys.IFTTT;
    private RequestQueue mQueue;

    public IFTTT(Context context, String makerKey){
        mQueue = Volley.newRequestQueue(context);
        mMakerKey = makerKey;
    }

    public void webhook(String event){
        webhook(event, new ArrayList<String>());
    }

    public void webhook(String event, final List<String> values){
        //Takes the first 3 values and sends them

        if(mMakerKey == null || event == null) return;

        String url = "https://maker.ifttt.com/trigger/"+event+"/with/key/"+mMakerKey;

        final JSONObject json = new JSONObject();
        if(values != null && values.size() > 0) {
            //Collect values into JSON
            for(int i = 0; i < values.size(); i++){
                try {
                    json.put("values" + (i + 1), values.get(i));
                }catch(JSONException e){
                    Log.d("IFTTT", "JSONException on value"+(i+1));
                }
            }
        }

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("IFTTT", "Response: "+response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("IFTTT", "IFTTT Error: "+error.getMessage());
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return json.toString().getBytes();
            }
        };

        mQueue.add(postRequest);
    }
}
